package org.opendaylight.yang.gen.v1.brocade.training.sli.provider.impl.rev140523;

import com.att.sli.SliProvider;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.com.att.networkadapter.rev150115.NetworkadapterService;

public class SliProviderModule extends org.opendaylight.yang.gen.v1.brocade.training.sli.provider.impl.rev140523.AbstractSliProviderModule {
    public SliProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver) {
        super(identifier, dependencyResolver);
    }

    public SliProviderModule(org.opendaylight.controller.config.api.ModuleIdentifier identifier, org.opendaylight.controller.config.api.DependencyResolver dependencyResolver, org.opendaylight.yang.gen.v1.brocade.training.sli.provider.impl.rev140523.SliProviderModule oldModule, java.lang.AutoCloseable oldInstance) {
        super(identifier, dependencyResolver, oldModule, oldInstance);
    }

    @Override
    public void customValidation() {
// add custom validation form module attributes here.
    }

    @Override
    public java.lang.AutoCloseable createInstance() {

        RpcProviderRegistry rpcRegistryDependency = getRpcRegistryDependency();
        NetworkadapterService networkadapterService = rpcRegistryDependency.getRpcService(NetworkadapterService.class);
        final SliProvider provider = new SliProvider(getRpcRegistryDependency(), networkadapterService);
        return new AutoCloseable() {


            public void close() throws Exception {
                provider.close();
            }
        };
    }


}
