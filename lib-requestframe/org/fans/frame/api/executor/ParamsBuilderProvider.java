package org.fans.frame.api.executor;

import org.fans.frame.api.packet.ApiRequest;

/**
 * {@link ParamsBuilder}
 * 
 * @author Ludaiqian
 * @since 1.0
 */
public interface ParamsBuilderProvider {

	public ParamsBuilder provide(ApiRequest request);
}
