package com.cmp.client.cli;
import com.cmp.service.Bootstrap;
import com.cmp.cloud.repo.*;
import com.cmp.cloud.api.*;
import com.cmp.protocol.api.*;

/** 本地演示用装配：CMP 与云端皆为内存实现，CLI 直接持有接口实例。 */
public final class DemoWiring {
    public final Bootstrap cmp = new Bootstrap();
    public final IndexRepo indexRepo = new IndexRepo();
    public final DocStoreRepo docStoreRepo = new DocStoreRepo();
    public final StateTableRepo stateTableRepo = new StateTableRepo();
    public final CloudIndexApi cloudIndex = new CloudIndexApiImpl(indexRepo, docStoreRepo);
    public final StateTableApi stateApi = new StateTableApiImpl(stateTableRepo);
    public final SearchApi searchApi = new SearchApiImpl(indexRepo, stateTableRepo);
}
