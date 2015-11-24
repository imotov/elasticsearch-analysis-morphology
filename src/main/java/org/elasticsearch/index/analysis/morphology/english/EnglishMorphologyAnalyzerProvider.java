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

package org.elasticsearch.index.analysis.morphology.english;

import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.apache.lucene.morphology.english.EnglishAnalyzer;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.index.settings.IndexSettingsService;

import java.io.IOException;

/**
 *
 */
public class EnglishMorphologyAnalyzerProvider extends AbstractIndexAnalyzerProvider<MorphologyAnalyzer> {

    private final MorphologyAnalyzer analyzer;

    @Inject
    public EnglishMorphologyAnalyzerProvider(Index index, IndexSettingsService indexSettingsService,
                                             @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettingsService.indexSettings(), name, settings);
        try {
            analyzer = new EnglishAnalyzer();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to load English morphology analyzer", ex);
        }
    }

    @Override
    public MorphologyAnalyzer get() {
        return this.analyzer;
    }
}
