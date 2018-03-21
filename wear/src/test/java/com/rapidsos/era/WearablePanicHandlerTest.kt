package com.rapidsos.era

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import org.junit.Test
import org.mockito.Mockito
import org.mockito.invocation.InvocationOnMock

/**
 * @author Josias Sena
 */
class WearablePanicHandlerTest {

    @Test
    fun panic() {
        val nodeApi = Mockito.mock(Wearable.NodeApi::class.java)
        val result = Mockito.mock(PendingResult::class.java) as PendingResult<NodeApi.GetConnectedNodesResult>
        val results = Mockito.mock(NodeApi.GetConnectedNodesResult::class.java)

        val node = Mockito.mock(Node::class.java)
        Mockito.`when`(node.id).thenReturn("fakeId")
        Mockito.`when`(node.displayName).thenReturn("fakeName")
        Mockito.`when`(node.isNearby).thenReturn(true)

        val nodes = mutableListOf<Node>(node)
        Mockito.`when`(results.nodes).thenReturn(nodes)

        Mockito.doAnswer { invocationOnMock: InvocationOnMock ->
            val callback = invocationOnMock.getArgument<ResultCallback<NodeApi.GetConnectedNodesResult>>(0)
            callback.onResult(results)

        }.`when`(result).setResultCallback(Mockito.any())

        Mockito.`when`(nodeApi.getConnectedNodes(Mockito.any<GoogleApiClient>())).thenReturn(result)

        val googleApiClient = Mockito.mock(GoogleApiClient::class.java)
        val handler = WearablePanicHandler(nodeApi, googleApiClient)
        handler.panic()

        Mockito.verify(results).nodes

        // Verifies that the node made it to the notifyDeviceOfAlertCreation method
        Mockito.verify(node).id
    }

}