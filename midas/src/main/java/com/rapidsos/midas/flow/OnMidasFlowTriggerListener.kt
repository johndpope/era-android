package com.rapidsos.midas.flow

/**
 * @author Josias Sena
 */
interface OnMidasFlowTriggerListener {

    /**
     * Called whe the flow has been initiated successfully
     */
    fun onSuccess()

    /**
     * Called when an error occurs at any time during the midas flow
     *
     * @param error the error message
     */
    fun onError(error: String)

}