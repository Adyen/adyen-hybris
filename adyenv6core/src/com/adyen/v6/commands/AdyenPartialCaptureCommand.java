package com.adyen.v6.commands;

import de.hybris.platform.payment.commands.PartialCaptureCommand;
import de.hybris.platform.payment.commands.request.PartialCaptureRequest;
import de.hybris.platform.payment.commands.result.CaptureResult;


public class AdyenPartialCaptureCommand extends AbstractAdyenCaptureCommand implements PartialCaptureCommand {
    /**
     * {@inheritDoc}
     *
     * @see de.hybris.platform.payment.commands.Command#perform(Object)
     */
    @Override
    public CaptureResult perform(final PartialCaptureRequest partialCaptureRequest) {
        return super.perform(partialCaptureRequest);
    }
}
