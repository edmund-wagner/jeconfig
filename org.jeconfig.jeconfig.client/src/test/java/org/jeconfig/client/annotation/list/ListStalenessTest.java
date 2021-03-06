/*
 * Copyright (c) 2011: Edmund Wagner, Wolfram Weidel
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the jeconfig nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.jeconfig.client.annotation.list;

import org.jeconfig.api.scope.DefaultScopeDescriptor;
import org.jeconfig.api.scope.GlobalScopeDescriptor;
import org.jeconfig.api.scope.IScopePath;
import org.jeconfig.api.scope.IScopePathBuilderFactory;
import org.jeconfig.client.AbstractConfigServiceTest;
import org.jeconfig.client.testconfigs.DummyStalenessNotifier;
import org.junit.Assert;
import org.junit.Test;

@SuppressWarnings("nls")
public class ListStalenessTest extends AbstractConfigServiceTest {
	@Override
	public void tearDown() {
		DummyStalenessNotifier.setMergingResult(null);
		super.tearDown();
	}

	@Test
	public void testUseParentOnStalenessBecauseOfDeletion() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(
				ListStalenessTestConfiguration.class);
		final IScopePath globalPath = builderFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();

		final ListStalenessTestConfiguration globalConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				globalPath);
		globalConfig.getProperty1().add("global");
		getConfigService().save(globalConfig);

		final ListStalenessTestConfiguration defaultConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				defaultPath);
		defaultConfig.getProperty1().add("default");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration userConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		userConfig.getProperty1().add("user");
		getConfigService().save(userConfig);

		getConfigService().delete(defaultPath, false);

		final ListStalenessTestConfiguration currentUserConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		Assert.assertEquals(globalConfig.getProperty1(), currentUserConfig.getProperty1());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfDiscardedProperties());
	}

	@Test
	public void testMergeAndUseChildOnStalenessBecauseOfDeletion() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(
				ListStalenessTestConfiguration.class);
		final IScopePath globalPath = builderFactory.annotatedPathUntil(GlobalScopeDescriptor.NAME).create();
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();

		final ListStalenessTestConfiguration globalConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				globalPath);
		globalConfig.getProperty2().add("global");
		getConfigService().save(globalConfig);

		final ListStalenessTestConfiguration defaultConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				defaultPath);
		defaultConfig.getProperty2().add("default");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration userConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		userConfig.getProperty2().add("user");
		getConfigService().save(userConfig);

		getConfigService().delete(defaultPath, false);

		final ListStalenessTestConfiguration currentUserConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		Assert.assertEquals(userConfig.getProperty2(), currentUserConfig.getProperty2());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfMergedProperties());
	}

	@Test
	public void testUseParentOnStalenessBecauseOfParentUpdate() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(
				ListStalenessTestConfiguration.class);
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();

		final ListStalenessTestConfiguration defaultConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				defaultPath);
		defaultConfig.getProperty1().add("default");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration userConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		userConfig.getProperty1().add("user");
		getConfigService().save(userConfig);

		defaultConfig.getProperty1().add("default2");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration currentUserConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		Assert.assertEquals(defaultConfig.getProperty1(), currentUserConfig.getProperty1());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfDiscardedProperties());
	}

	@Test
	public void testMergeAndUseChildOnStalenessBecauseOfParentUpdate() {
		final IScopePathBuilderFactory builderFactory = getConfigService().getScopePathBuilderFactory(
				ListStalenessTestConfiguration.class);
		final IScopePath defaultPath = builderFactory.annotatedPathUntil(DefaultScopeDescriptor.NAME).create();

		final ListStalenessTestConfiguration defaultConfig = getConfigService().load(
				ListStalenessTestConfiguration.class,
				defaultPath);
		defaultConfig.getProperty2().add("default");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration userConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		userConfig.getProperty2().add("user");
		getConfigService().save(userConfig);

		defaultConfig.getProperty2().add("default2");
		getConfigService().save(defaultConfig);

		final ListStalenessTestConfiguration currentUserConfig = getConfigService().load(ListStalenessTestConfiguration.class);
		Assert.assertEquals(userConfig.getProperty2(), currentUserConfig.getProperty2());
		Assert.assertEquals(1, DummyStalenessNotifier.getMergingResult().getNumberOfMergedProperties());
	}
}
