package com.cmp.protocol.api;
import com.cmp.protocol.dto.StateTableDTO;
/** 云端：状态表 S 的存取接口 */
public interface StateTableApi {
    void put(StateTableDTO.PutRequest req);
    StateTableDTO.GetResponse get(StateTableDTO.GetRequest req);
}
