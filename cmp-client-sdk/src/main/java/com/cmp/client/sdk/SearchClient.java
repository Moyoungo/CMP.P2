package com.cmp.client.sdk;

import com.cmp.protocol.api.SearchApi;
import com.cmp.protocol.api.StateTableApi;
import com.cmp.protocol.dto.SearchDTO;
import com.cmp.protocol.dto.StateTableDTO;
import com.cmp.core.crypto.HashUtil;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 搜索客户端（超级详细日志版）：
 * 逐批检索，并在每一步打印【中文解释】告诉你：当前用到的状态、S 表里拿到的 SK、云端 hasMore 的含义、
 * 为什么要前推下一状态、何时停止，等等。
 */
public final class SearchClient {
    private final SearchApi searchApi;
    private final StateTableApi stateApi;

    public SearchClient(SearchApi searchApi, StateTableApi stateApi){
        this.searchApi = searchApi;
        this.stateApi = stateApi;
    }

    /**
     * 逐批拉取。返回：去重后的文档ID列表 + 是否仍可能有更多结果。
     * <p>日志总览：</p>
     * 1) 打印关键上下文（sid / ds / clientId / tokenW 长度 / ST0 的 H1）<br>
     * 2) 对每一批：
     *    - 打印当前 ST_t 的 H1 值（等价于这批的“时间点/批次标识”）<br>
     *    - 预读 S 表：看 SK_t 是不是 null 或空数组（空数组是“断点哨兵”）<br>
     *    - 发起搜索、打印返回的 docIds 与 hasMore（注意：云端的 hasMore==S表是否存在该 h1st 的条目；空数组也算“存在”）<br>
     *    - 前推状态：若 StateAdvance 返回 null，说明到达链尾（sk==null 或 sk.length==0），停止<br>
     * 3) 打印汇总与停止原因解释
     */
    public Result searchAll(GrantSession grant, String keyword, int maxBatches){
        // 1) 关键上下文
        byte[] tokenW = TokenDeriver.tokenFor(grant, keyword);
        log("");
        log("【阶段 - 开始检索】本次检索关键字 = \"%s\"", keyword);
        log("【说明】客户端使用授权（Grant）派生 tokenW，并按状态链逐批检索。");
        logKV("数据源 sid", grant.sid);
        logKV("数据集 ds", grant.ds);
        logKV("客户端 clientId", grant.clientId);
        logKV("tokenW 长度", tokenW.length + " 字节");
        logKV("ST0 的 H1", hex(HashUtil.H1(grant.st0)));

        byte[] st = grant.st0;
        Set<Integer> acc = new LinkedHashSet<>();
        int batch = 0;

        // 2) 逐批检索
        while (st != null && batch < maxBatches){
            byte[] h1st = HashUtil.H1(st);
            log("");
            log("▶▶ 批次 %d | 使用当前状态 ST_%d", batch, batch);
            logKV("H1(ST_t)", hex(h1st));
            log("【解释】H1(ST_t) 是这批检索的“时间点/批次指纹”，也是 S 表查询的键。");

            // 2.1 预读 S 表条目，看看 SK_t 是不是断点（空数组也当断点哨兵）
            StateTableDTO.GetResponse sresp = stateApi.get(new StateTableDTO.GetRequest(h1st));
            byte[] sk = (sresp == null ? null : sresp.sk);
            if (sk == null) {
                log("S 表返回 SK_t = null ——【含义】链尾（没有下一个状态）。");
            } else {
                logKV("S 表返回 SK_t 长度", sk.length + " 字节" + (sk.length == 0 ? "（空数组=断点哨兵）" : ""));
            }

            // 2.2 向云端发起本批搜索
            var req = new SearchDTO.Request(grant.sid, grant.ds, tokenW, st);
            var resp = searchApi.search(req);
            log("向云端 SearchApi 发起搜索：sid=%s, ds=%s, tokenW(长度=%d), 使用 ST_t。",
                    grant.sid, grant.ds, tokenW.length);
            logKV("云端返回 docIds", String.valueOf(resp.docIds));
            logKV("云端返回 hasMore", String.valueOf(resp.hasMore));
            log("【解释】云端 hasMore 的判定：只要 S 表存在 H1(ST_t) 的条目就为 true；" +
                    "如果我们用“空数组”表示断点，云端 hasMore 仍可能为 true。实际是否还能继续，由客户端前推决定。");

            if (resp.docIds != null) acc.addAll(resp.docIds);

            // 2.3 前推下一状态：真正决定“是否还能继续”
            byte[] stNext = StateAdvance.next(stateApi, grant.sid, grant.ds, grant.alpha, st);
            if (stNext == null) {
                log("StateAdvance.next(...) 返回 null ——【到达链尾】");
                log("【解释】原因通常是：S 表中该批次的 SK_t 为空(null)或空数组，这两种都视为链尾。");
                st = null; // 结束循环
                break;
            } else {
                logKV("计算得到下一状态 H1(ST_{t+1})", hex(HashUtil.H1(stNext)));
                log("【解释】下一批会使用 ST_{t+1} 来查询索引；若云端 hasMore=false，即使还能前推我们也会停止。");
                st = stNext;
                batch++;
            }

            if (!resp.hasMore) {
                log("云端 hasMore=false ——【含义】云端认为这一批已经没有更多可拿的结果。");
                log("【策略】为了与云端一致，此处停止（即使还能前推）。");
                break;
            }
        }

        // 3) 汇总输出
        List<Integer> out = new ArrayList<>(acc);
        boolean mayHaveMore = (st != null);
        log("");
        log("【阶段 - 检索结束】");
        logKV("最终去重后的 docIds", String.valueOf(out));
        logKV("mayHaveMore", String.valueOf(mayHaveMore));
        log("【解释】mayHaveMore=true 表示我们尚未到达链尾（还有未遍历的状态）；" +
                "false 表示已经到链尾或被云端 hasMore=false 提前终止。");
        log("");

        return new Result(out, mayHaveMore);
    }

    public static final class Result {
        public final List<Integer> docIds;
        public final boolean mayHaveMore;
        public Result(List<Integer> docIds, boolean mayHaveMore){
            this.docIds = docIds; this.mayHaveMore = mayHaveMore;
        }
    }

    // ====== 辅助输出 ======
    private static void log(String fmt, Object... args){
        System.out.println(String.format(fmt, args));
    }
    private static void logKV(String key, String val){
        System.out.println(String.format("  - %s：%s", key, val));
    }
    private static String hex(byte[] b){
        if (b == null) return "null";
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
