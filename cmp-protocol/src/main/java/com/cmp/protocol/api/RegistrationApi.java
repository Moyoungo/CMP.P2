package com.cmp.protocol.api;
import com.cmp.protocol.dto.RegisterSource;
public interface RegistrationApi {
    RegisterSource.Response registerSource(RegisterSource.Request req);
}
