package com.cmp.service.repo;
import com.cmp.core.model.Grant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/** 授权登记：key=(clientId|sid|ds) -> Grant */
public final class GrantRegistryRepo {
    private final Map<String, Grant> map = new ConcurrentHashMap<>();
    private static String key(String clientId, String sid, String ds){ return clientId+"|"+sid+"|"+ds; }
    public void put(Grant g){ map.put(key(g.clientId,g.sid,g.ds), g); }
    public Optional<Grant> get(String clientId, String sid, String ds){
        return Optional.ofNullable(map.get(key(clientId,sid,ds)));
    }
}
