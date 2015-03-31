/*
 * Copyright 2012 Igor Motov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.elasticsearch.plugin.analysis.morphology;

import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.morphology.MorphologyAnalysisBinderProcessor;
import org.elasticsearch.indices.analysis.morphology.MorphologyIndicesAnalysisModule;
import org.elasticsearch.plugins.AbstractPlugin;

import java.util.Collection;

/**
 *
 */
public class AnalysisMorphologyPlugin extends AbstractPlugin {

    @Override
    public String name() {
        return "analysis-morphology";
    }

    @Override
    public String description() {
        return "Morphology analysis support";
    }

    @Override
    public Collection<Class<? extends Module>> modules() {
        return ImmutableList.<Class<? extends Module>>of(MorphologyIndicesAnalysisModule.class);
    }

    public void onModule(AnalysisModule module) {
        module.addProcessor(new MorphologyAnalysisBinderProcessor());
    }
}
