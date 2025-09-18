package com.cmp.protocol.api;
import com.cmp.protocol.dto.SearchDTO;
/** 云端：搜索接口（按状态回溯直到断点） */
public interface SearchApi {
    SearchDTO.Response search(SearchDTO.Request req);
}
