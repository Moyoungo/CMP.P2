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
import java.util.*;

/**
 * CLI：支持一个“demo”命令跑通端到端：
 * 1) 本地装配 CMP/Cloud；
 * 2) 注册数据源并发批次（3 段）；
 * 3) 客户端购买关键字（无需相似度）；
 * 4) 写入状态链；
 * 5) 客户端按授权范围检索；
 *
 * 用法：
 *   java -jar cmp-client-cli.jar demo
 */
public final class ClientMain {
    public static void main(String[] args) {
        if (args.length==0 || "demo".equalsIgnoreCase(args[0])) {
            runDemo();
            return;
        }
        System.out.println("Usage: demo");
    }

    private static void runDemo(){
        System.out.println("== CMP/Cloud local demo ==");
        DemoWiring env = new DemoWiring();
        RegistrationApi reg = env.cmp.registration;
        GrantApi grantApi = env.cmp.grant;
        VectorIngestApi vecIn = env.cmp.vectorIngest;
        SimilarityApi simApi = env.cmp.similarity;
        CloudIndexApi cloudIndex = env.cloudIndex;
        StateTableApi stateApi = env.stateApi;
        SearchApi searchApi = env.searchApi;

        // 1) 注册数据源
        String sid = "SRC_A";
        System.out.println("Register source: "+sid);
        byte[] ksrc = reg.registerSource(new RegisterSource.Request(sid)).kSrc;

        // 2) 准备数据集与批次（3 段）
        String ds = "rabbits";
        String clientId = "client1";
        int batches = 3;

        // 3) 客户端直接购买某关键字（无需相似度）
        String keyword = "medication";
        System.out.println("Client buys keyword: "+keyword);
        GrantContract.Response grant = grantApi.issueContract(
            new GrantContract.Request(clientId, sid, ds, new RangeSpec(0, null, true),
                                      Grant.Mode.PER_KEYWORD, java.util.List.of(keyword))
        );
        GrantSession session = new GrantSession(clientId, sid, ds, grant.st0, grant.alpha, Grant.Mode.PER_KEYWORD, null, grant.perKeywordKLabels);

        // 4) 写入状态链（与授权对齐）
        StateChainBuilder.writeChain(stateApi, sid, ds, session.alpha, session.st0, batches);

        // 5) 数据源侧上送 3 个批次
        var encryptor = new Encryptor(ksrc);
        var vectorizer = new SimpleVectorizer(384, 128, 1.0f);
        var sender = new Sender(env.cloudIndex, vecIn);
        var ingest = new IngestService(encryptor, vectorizer, sender);

        for (int t=0; t<batches; t++){
            List<Document> docs = makeDocs(t);
            Ack a = ingest.runBatch(sid, ds, t, new State(session.st0), session.perKeywordKLabels.get(keyword), docs);
            System.out.println("Batch "+t+" ingested: "+a.ok+" ("+docs.size()+" docs)");
        }

        // 6) 客户端检索（按授权前推）
        var sdk = new SearchClient(searchApi, stateApi);
        var res = sdk.searchAll(session, keyword, 10);
        System.out.println("Search result docIds="+res.docIds+" ; mayHaveMore="+res.mayHaveMore);
        System.out.println("== done ==");
    }

    private static List<Document> makeDocs(int t){
        List<Document> docs = new ArrayList<>();
        if (t==0){
            docs.add(new Document(1, "Rabbit medication phase I: baseline vitals", List.of("medication","rabbit")));
            docs.add(new Document(2, "General notes on rabbit diet", List.of("diet","rabbit")));
        } else if (t==1){
            docs.add(new Document(3, "Rabbit medication phase II: dosage adjusted", List.of("medication","rabbit")));
            docs.add(new Document(4, "Observations of side effects", List.of("medication","rabbit")));
        } else {
            docs.add(new Document(5, "Rabbit medication phase III: tapering off", List.of("medication","rabbit")));
            docs.add(new Document(6, "Final report summary", List.of("report")));
        }
        return docs;
    }
}
