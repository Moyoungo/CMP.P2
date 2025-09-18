package com.cmp.client.cli;

import com.cmp.protocol.api.*;
import com.cmp.protocol.dto.*;
import com.cmp.protocol.types.Ack;
import com.cmp.core.model.*;
import com.cmp.client.sdk.*;
import com.cmp.agent.ingest.IngestService;
import com.cmp.agent.encrypt.Encryptor;
import com.cmp.agent.vector.SimpleVectorizer;
import com.cmp.agent.sender.Sender;
import com.cmp.agent.model.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * CLI：支持一个“demo”命令跑通端到端：
 * 1) 本地装配 CMP/Cloud；
 * 2) 注册数据源并发批次（3 段）；
 * 3) 客户端购买关键字（无需相似度）；
 * 4) 写入状态链；
 * 5) 客户端按授权范围检索；
 *
 * 用法：
 *   无参数或 demo 参数均可运行： java -cp ... com.cmp.client.cli.ClientMain [demo]
 */
public final class ClientMain {

    public static void main(String[] args) {
        if (args.length == 0 || "demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }
        System.out.println("Usage: demo");
    }

    private static void runDemo() {
        System.out.println("== CMP/Cloud local demo ==");

        // 0) 本地内存装配（见 DemoWiring）
        DemoWiring env = new DemoWiring();
        RegistrationApi reg   = env.cmp.registration;
        GrantApi        grantApi = env.cmp.grant;
        VectorIngestApi vecIn = env.cmp.vectorIngest;
        StateTableApi   stateApi = env.stateApi;
        SearchApi       searchApi = env.searchApi;

        // 1) 注册数据源
        String sid = "SRC_A";
        System.out.println("Register source: " + sid);
        byte[] ksrc = reg.registerSource(new RegisterSource.Request(sid)).kSrc;

        // 2) 数据集参数
        String ds = "rabbits";
        String clientId = "client1";
        int batches = 3;
        String keyword = "medication";

        // === 数据源侧密钥派生：加密用 K_enc；建索引用 K_LABEL_DS（数据集级） ===
        byte[] kEnc     = com.cmp.core.keys.Kdf.kEnc(ksrc);
        byte[] kLabelDS = com.cmp.core.keys.Kdf.kLabelDS(ksrc, sid, ds);

        // 3) 客户端直接购买某关键字（PER_KEYWORD：服务端返回的是 tokenW，而不是 key）
        GrantContract.Response grant = grantApi.issueContract(
                new GrantContract.Request(
                        clientId, sid, ds,
                        new RangeSpec(0, null, true),
                        Grant.Mode.PER_KEYWORD,
                        java.util.List.of(keyword)
                )
        );
        GrantSession session = new GrantSession(
                clientId, sid, ds, grant.st0, grant.alpha,
                Grant.Mode.PER_KEYWORD, null, grant.perKeywordKLabels
        );

        // 4) 写入状态链，并拿到每个 ST_t（云端 S 表：H1(ST_t) -> SK_t；末尾置空=断点）
        List<byte[]> states = StateChainBuilder.buildAndWriteChain(
                stateApi, sid, ds, session.alpha, session.st0, batches
        );
        // states 包含 ST_0..ST_batches（我们逐批用 ST_t）

        // 5) 数据源侧依次上送 3 个批次（每批使用对应 ST_t 与 K_LABEL_DS；加密用 K_enc）
        var encryptor = new Encryptor(kEnc);
        var vectorizer = new SimpleVectorizer(384, 128, 1.0f);
        var sender = new Sender(env.cloudIndex, vecIn);
        var ingest = new IngestService(encryptor, vectorizer, sender);

        for (int t = 0; t < batches; t++) {
            List<Document> docs = makeDocs(t);
            // 注意：每批都用 states.get(t) 作为 ST_t；kLabel 参数传 K_LABEL_DS
            Ack a = ingest.runBatch(sid, ds, t, new State(states.get(t)), kLabelDS, docs);
            System.out.println("Batch " + t + " ingested: " + a.ok + " (" + docs.size() + " docs)");
        }

        // 6) 客户端检索（按授权前推），此时能检到三批
        var sdk = new SearchClient(searchApi, stateApi);
        var res = sdk.searchAll(session, keyword, 10);
        System.out.println("Search result docIds=" + res.docIds + " ; mayHaveMore=" + res.mayHaveMore);
        System.out.println("== done ==");
    }

    private static List<Document> makeDocs(int t) {
        List<Document> docs = new ArrayList<>();
        if (t == 0) {
            docs.add(new Document(1, "Rabbit medication phase I: baseline vitals", List.of("medication", "rabbit")));
            docs.add(new Document(2, "General notes on rabbit diet", List.of("diet", "rabbit")));
        } else if (t == 1) {
            docs.add(new Document(3, "Rabbit medication phase II: dosage adjusted", List.of("medication", "rabbit")));
            docs.add(new Document(4, "Observations of side effects", List.of("medication", "rabbit")));
        } else {
            docs.add(new Document(5, "Rabbit medication phase III: tapering off", List.of("medication", "rabbit")));
            docs.add(new Document(6, "Final report summary", List.of("report")));
        }
        return docs;
    }
}
