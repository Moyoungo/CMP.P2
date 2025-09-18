package com.cmp.protocol.api;
import com.cmp.protocol.dto.GrantContract;
/** CMP：发放授权（支持直接购买关键字，不依赖相似度） */
public interface GrantApi {
    GrantContract.Response issueContract(GrantContract.Request req);
}
