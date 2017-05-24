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

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;

/**
 * Provider for russian/english morphology analyzer
 */
public class MorphologyAnalyzerProvider extends AbstractIndexAnalyzerProvider<MorphologyAnalyzer> {

    private final MorphologyAnalyzer analyzer;

    public MorphologyAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name,
                                      Settings settings, LuceneMorphology luceneMorphology) {
        super(indexSettings, name, settings);
        analyzer = new MorphologyAnalyzer(luceneMorphology);
    }

    @Override
    public MorphologyAnalyzer get() {
        return this.analyzer;
    }
}
