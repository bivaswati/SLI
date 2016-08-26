/**
* (C)2015 Brocade Communications Systems, Inc.
* 130 Holger Way, San Jose, CA 95134.
* All rights reserved.
*
* @author Anton Ivanov <aivanov@brocade.com>
* Brocade, the B-wing symbol, Brocade Assurance, ADX, AnyIO, DCX, Fabric OS,
* FastIron, HyperEdge, ICX, MLX, MyBrocade, NetIron, OpenScript, VCS, VDX, and
* Vyatta are registered trademarks, and The Effortless Network and the On-Demand
* Data Center are trademarks of Brocade Communications Systems, Inc., in the
* United States and in other countries. Other brands and product names mentioned
* may be trademarks of others.
*
* Use of the software files and documentation is subject to license terms.
*/
package com.att.sli;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.sql.*;
import com.att.sli.blacknode.BlackNode;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.sal.binding.api.BindingAwareBroker;
import org.opendaylight.controller.sal.binding.api.RpcProviderRegistry;
import org.opendaylight.yang.gen.v1.brocade.sample.rev150115.*;
import org.opendaylight.yang.gen.v1.com.att.networkadapter.rev150115.NetworkadapterService;
import org.opendaylight.yang.gen.v1.com.att.networkadapter.rev150115.SendCommandInputBuilder;
import org.opendaylight.yang.gen.v1.com.att.networkadapter.rev150115.SendCommandOutput;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines a base implementation for your provider. This class extends from a helper class
 * which provides storage for the most commonly used components of the MD-SAL. Additionally the
 * base class provides some basic logging and initialization / clean up methods.
 *
 * To use this, copy and paste (overwrite) the following method into the TestApplicationProviderModule
 * class which is auto generated under src/main/java in this project
 * (created only once during first compilation):
 *
 * <pre>

    @Override
    public java.lang.AutoCloseable createInstance() {

		 com.att.sli.TypeEnumHelper helper = new com.att.sli.TypeEnumHelper();
         helper.setSchemaService( getRootSchemaServiceDependency().getGlobalContext() );
		 helper.initialize();

         final SliProvider provider = new SliProvider();
		 provider.setTypeHelper( helper );
         provider.setDataBroker( getDataBrokerDependency() );
         provider.setNotificationService( getNotificationServiceDependency() );
         provider.setRpcRegistry( getRpcRegistryDependency() );
         provider.initialize();

         return new AutoCloseable() {

            @Override
            public void close() throws Exception {
                //TODO: CLOSE ANY REGISTRATION OBJECTS CREATED USING ABOVE BROKER/NOTIFICATION
                //SERVIE/RPC REGISTRY
                provider.close();
            }
        };
    }


    </pre>
 */
public class SliProvider implements AutoCloseable, SliService {

	private List<AutoCloseable> closeList = new LinkedList<AutoCloseable>();
    private final Logger log = LoggerFactory.getLogger( SliProvider.class );
    private final String appName = "Sli";
    public static NetworkadapterService networkadapterService = null;
    private final ExecutorService executor;
    protected DataBroker dataBroker;
    protected RpcProviderRegistry rpcRegistry;
    protected BindingAwareBroker.RpcRegistration<SliService> rpcRegistration;

    public SliProvider(RpcProviderRegistry rpcProviderRegistry, NetworkadapterService networkadapterService) {
        this.log.info( "Creating provider for " + appName );
        executor = Executors.newFixedThreadPool(1);
        rpcRegistry = rpcProviderRegistry;
        this.networkadapterService = networkadapterService;
        initialize();
    }

    public void initialize(){
        log.info( "Initializing provider for " + appName );
        rpcRegistration = rpcRegistry.addRpcImplementation(SliService.class, this);
        initializeChild();
        log.info( "Initialization complete for " + appName );
    }

    protected void initializeChild() {
        //Override if you have custom initialization intelligence
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
        rpcRegistration.close();
        log.info( "Successfully closed provider for " + appName );
    }


    @Override
	public Future<RpcResult<DoConfigureOutput>> doConfigure(DoConfigureInput input) {

        DoConfigureOutputBuilder doConfigureOutputBuilder = new DoConfigureOutputBuilder();
        Future<RpcResult<SendCommandOutput>> resultFuture = sendCommand(input);
        try {
            String result =resultFuture.get().getResult().getStatus();
            if (result.equalsIgnoreCase("Success")) {
                doConfigureOutputBuilder.setStatus("Success");
            }
            else {
                doConfigureOutputBuilder.setStatus("Failed");
            }

        } catch (InterruptedException e) {
            doConfigureOutputBuilder.setStatus("Failed");
            e.printStackTrace();
        } catch (ExecutionException e) {
            doConfigureOutputBuilder.setStatus("Failed");
            e.printStackTrace();
        }

        return RpcResultBuilder.success(doConfigureOutputBuilder.build()).buildFuture();

    }

    // Function will set the required input and call the Network Adapter's RPC
    public Future<RpcResult<SendCommandOutput>> sendCommand(DoConfigureInput input) {

        SendCommandInputBuilder sendCommandInputBuilder = new SendCommandInputBuilder();
        sendCommandInputBuilder.setUserName(input.getDeviceUserName());
        sendCommandInputBuilder.setPassword(input.getDevicePassword());
        sendCommandInputBuilder.setIp(input.getDeviceIp());
        sendCommandInputBuilder.setOperationType(input.getDeviceOperationType());
        //Communicating with Black Node to Fetch data from DB
        BlackNode blackNode = new BlackNode();
        String description = blackNode.getDescription(input.getDeviceConfigureType());
        sendCommandInputBuilder.setDescription(description);
        sendCommandInputBuilder.setInterfaceName(input.getDeviceInterfaceName());
        return networkadapterService.sendCommand(sendCommandInputBuilder.build());
    }

 /*   @Override
    public Future<RpcResult<FetchDataOutput>> fetchData(FetchDataInput input) { 	
    
    FetchDataOutputBuilder fetchDataOutput= new BlackNode().getData(input);
    return RpcResultBuilder.success(fetchDataOutput.build()).buildFuture();   
    }
*/
}
