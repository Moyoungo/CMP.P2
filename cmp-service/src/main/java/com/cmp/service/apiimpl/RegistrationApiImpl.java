package com.cmp.service.apiimpl;
import com.cmp.protocol.api.RegistrationApi;
import com.cmp.protocol.dto.RegisterSource;
import com.cmp.service.repo.SourceRegistry;
import com.cmp.core.crypto.AesGcm;

/** 注册数据源：生成随机 K_src 并保存 */
public final class RegistrationApiImpl implements RegistrationApi {
    private final SourceRegistry registry;
    public RegistrationApiImpl(SourceRegistry registry){ this.registry = registry; }
    @Override public RegisterSource.Response registerSource(RegisterSource.Request req) {
        byte[] ksrc = AesGcm.randomKey();
        registry.put(req.sid, ksrc);
        return new RegisterSource.Response(ksrc);
    }
}
