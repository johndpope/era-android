package com.rapidsos.era

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.wearable.MessageApi
import com.google.android.gms.wearable.NodeApi
import com.google.android.gms.wearable.Wearable
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.debug
import java.nio.charset.StandardCharsets

/**
 * @author Josias Sena
 */
class WearablePanicHandler(private val nodeApi: NodeApi,
                           private val googleApiClient: GoogleApiClient) : AnkoLogger,
        ResultCallback<MessageApi.SendMessageResult> {

    companion object {
        private const val CREATE_ALERT_MESSAGE_PATH = "/"
        private const val CREATE_ALERT = "create_alert"
    }

    /**
     * Perform the panic functionality by notifying the device that the button was pressed.
     */
    fun panic() {
        val nodes = nodeApi.getConnectedNodes(googleApiClient)

        nodes.setResultCallback { getConnectedNodesResult: NodeApi.GetConnectedNodesResult ->
            val nodeList = getConnectedNodesResult.nodes

            nodeList.forEach { node ->
                notifyDeviceOfAlertCreation(node.id)
            }
        }
    }

    private fun notifyDeviceOfAlertCreation(nodeId: String) {
        val messageResult = Wearable.MessageApi
                .sendMessage(googleApiClient, nodeId, CREATE_ALERT_MESSAGE_PATH,
                        CREATE_ALERT.toByteArray(StandardCharsets.UTF_8))

        messageResult?.setResultCallback(this)
    }

    override fun onResult(sendMessageResult: MessageApi.SendMessageResult) {
        debug("onResult() called with: sendMessageResult = [${sendMessageResult.status}]")
    }

}