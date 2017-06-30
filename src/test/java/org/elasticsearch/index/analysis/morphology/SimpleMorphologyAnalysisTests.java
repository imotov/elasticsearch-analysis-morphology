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
import org.apache.lucene.morphology.english.EnglishLuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.elasticsearch.common.io.FastStringReader;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.analysis.NamedAnalyzer;
import org.elasticsearch.plugin.analysis.morphology.AnalysisMorphologyPlugin;
import org.elasticsearch.test.ESTestCase;

import java.io.IOException;
import java.io.StringReader;

import static org.hamcrest.Matchers.instanceOf;

public class SimpleMorphologyAnalysisTests extends ESTestCase {

    private TestAnalysis getAnalysisService() throws IOException {
        Settings indexSettings = Settings.builder()
                .put("index.analysis.analyzer.test.type", "custom")
                .put("index.analysis.analyzer.test.tokenizer", "standard")
                .putArray("index.analysis.analyzer.test.filter", "lowercase", "russian_morphology")
                .build();

        return createTestAnalysis(new Index("test", "_na_"), indexSettings, new AnalysisMorphologyPlugin());
    }

    public static void assertSimpleTSOutput(TokenStream stream, String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            assertTrue("got extra term: " + termAttr.toString(), i < expected.length);
            assertEquals("expected different term at index " + i, expected[i], termAttr.toString());
            i++;
        }
        assertEquals("not all tokens produced", expected.length, i);
    }

    public void testMorphologyAnalysis() throws Exception {
        TestAnalysis testAnalysis = getAnalysisService();

        NamedAnalyzer russianAnalyzer = testAnalysis.indexAnalyzers.get("russian_morphology");
        assertThat(russianAnalyzer.analyzer(), instanceOf(MorphologyAnalyzer.class));
        assertSimpleTSOutput(russianAnalyzer.tokenStream("test", new StringReader("тест")), new String[] {"тест", "тесто"});

        NamedAnalyzer englishAnalyzer = testAnalysis.indexAnalyzers.get("english_morphology");
        assertThat(englishAnalyzer.analyzer(), instanceOf(MorphologyAnalyzer.class));
        assertSimpleTSOutput(englishAnalyzer.tokenStream("test", new StringReader("gone")), new String[]{"gone", "go"});
    }

    public void testPm() throws Exception {
        LuceneMorphology russianLuceneMorphology = new RussianLuceneMorphology();
        LuceneMorphology englishLuceneMorphology = new EnglishLuceneMorphology();

        MorphologyAnalyzer russianAnalyzer = new MorphologyAnalyzer(russianLuceneMorphology);
        TokenStream stream = russianAnalyzer.tokenStream("name", new FastStringReader("тест пм тест"));
        MorphologyFilter englishFilter = new MorphologyFilter(stream, englishLuceneMorphology);
        assertSimpleTSOutput(englishFilter, new String[] {"тест", "тесто", "пм", "тест", "тесто"});
    }
}
