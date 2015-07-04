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

package org.elasticsearch.index.analysis.morphology;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.analyzer.MorphologyAnalyzer;
import org.apache.lucene.morphology.analyzer.MorphologyFilter;
import org.apache.lucene.morphology.english.EnglishAnalyzer;
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianAnalyzer;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.elasticsearch.Version;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.common.inject.Injector;
import org.elasticsearch.common.inject.ModulesBuilder;
import org.elasticsearch.common.io.FastStringReader;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.SettingsModule;
import org.elasticsearch.env.Environment;
import org.elasticsearch.env.EnvironmentModule;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.IndexNameModule;
import org.elasticsearch.index.analysis.AnalysisModule;
import org.elasticsearch.index.analysis.AnalysisService;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.index.settings.IndexSettingsModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisModule;
import org.elasticsearch.indices.analysis.IndicesAnalysisService;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.elasticsearch.common.settings.ImmutableSettings.Builder.EMPTY_SETTINGS;
import static org.hamcrest.Matchers.instanceOf;

/**
 *
 */
public class SimpleMorphologyAnalysisTests {

    private AnalysisService getAnalysisService() {
        Index index = new Index("test");

        Settings settings = ImmutableSettings.settingsBuilder()
                .put(IndexMetaData.SETTING_VERSION_CREATED, Version.CURRENT)
                .build();


        Injector parentInjector = new ModulesBuilder().add(new SettingsModule(settings),
                new EnvironmentModule(new Environment(settings)), new IndicesAnalysisModule()).createInjector();
        Injector injector = new ModulesBuilder().add(
                new IndexSettingsModule(index, settings),
                new IndexNameModule(index),
                new AnalysisModule(settings, parentInjector.getInstance(IndicesAnalysisService.class))
                        .addProcessor(new MorphologyAnalysisBinderProcessor()))
                .createChildInjector(parentInjector);

        return injector.getInstance(AnalysisService.class);

    }

    public static void assertSimpleTSOutput(TokenStream stream, String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        Assert.assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            Assert.assertTrue(i < expected.length, "got extra term: " + termAttr.toString());
            Assert.assertEquals(termAttr.toString(), expected[i], "expected different term at index " + i);
            i++;
        }
        Assert.assertEquals(i, expected.length, "not all tokens produced");
    }

    @Test
    public void testMorphologyAnalysis() throws Exception {
        AnalysisService analysisService = getAnalysisService();

        NamedAnalyzer russianAnalyzer = analysisService.analyzer("russian_morphology");
        MatcherAssert.assertThat(russianAnalyzer.analyzer(), instanceOf(RussianAnalyzer.class));
        assertSimpleTSOutput(russianAnalyzer.tokenStream("test", new StringReader("тест")), new String[] {"тест", "тесто"});

        NamedAnalyzer englishAnalyzer = analysisService.analyzer("english_morphology");
        MatcherAssert.assertThat(englishAnalyzer.analyzer(), instanceOf(EnglishAnalyzer.class));
        assertSimpleTSOutput(englishAnalyzer.tokenStream("test", new StringReader("gone")), new String[]{"gone", "go"});
    }

    @Test
    public void testPm() throws Exception {
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();

        MorphologyAnalyzer russianAnalyzer = new MorphologyAnalyzer(russianLuceneMorphology);
        TokenStream stream = russianAnalyzer.tokenStream("name", new FastStringReader("тест пм тест"));
        MorphologyFilter englishFilter = new MorphologyFilter(stream, englishLuceneMorphology);
        assertSimpleTSOutput(englishFilter, new String[] {"тест", "тесто", "пм", "тест", "тесто"});
    }

}
