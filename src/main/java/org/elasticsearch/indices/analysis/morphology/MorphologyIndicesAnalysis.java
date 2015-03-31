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

package org.elasticsearch.indices.analysis.morphology;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;
import org.apache.lucene.morphology.english.EnglishAnalyzer;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.elasticsearch.common.component.AbstractComponent;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.analysis.AnalyzerScope;
import org.elasticsearch.index.analysis.PreBuiltAnalyzerProviderFactory;
import org.elasticsearch.index.analysis.PreBuiltTokenFilterFactoryFactory;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;

import java.io.IOException;

/**
 * Registers indices level analysis components so, if not explicitly configured,
 * will be shared among all indices.
 */
public class MorphologyIndicesAnalysis extends AbstractComponent {

    @Inject
    public MorphologyIndicesAnalysis(Settings settings,
                                     IndicesAnalysisService indicesAnalysisService) {
        super(settings);
        try {
            indicesAnalysisService.analyzerProviderFactories().put("russian_morphology",
                    new PreBuiltAnalyzerProviderFactory("russian_morphology", AnalyzerScope.INDICES,
                            new RussianAnalyzer()));

            indicesAnalysisService.analyzerProviderFactories().put("english_morphology",
                    new PreBuiltAnalyzerProviderFactory("english_morphology", AnalyzerScope.INDICES,
                            new EnglishAnalyzer()));

            indicesAnalysisService.tokenFilterFactories().put("russian_morphology",
                    new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                        private final LuceneMorphology luceneMorph = new RussianLuceneMorphology();

                        @Override
                        public String name() {
                            return "russian_morphology";
                        }

                        @Override
                        public TokenStream create(TokenStream tokenStream) {
                            return new MorphologyFilter(tokenStream, luceneMorph);
                        }
                    }));

            indicesAnalysisService.tokenFilterFactories().put("english_morphology",
                    new PreBuiltTokenFilterFactoryFactory(new TokenFilterFactory() {
                        private final LuceneMorphology luceneMorph = new EnglishLuceneMorphology();

                        @Override
                        public String name() {
                            return "english_morphology";
                        }

                        @Override
                        public TokenStream create(TokenStream tokenStream) {
                            return new MorphologyFilter(tokenStream, luceneMorph);
                        }
                    }));

        } catch (IOException ex) {
            logger.warn("Cannot register indices level morphology analyzers", ex);
        }

    }
}
