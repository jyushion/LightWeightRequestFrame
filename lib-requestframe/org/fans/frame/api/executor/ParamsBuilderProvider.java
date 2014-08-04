package org.fans.frame.api.executor;

import org.fans.frame.api.packet.ApiRequest;

/**
 * {@link ParamsBuilder}
 * 
 * @author Ludaiqian
 * 
 */
public interface ParamsBuilderProvider {

	public ParamsBuilder provide(ApiRequest request);
}
