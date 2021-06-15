h1. This project is no longer supported

Elasticsearch 5.6.x is not maintained since March of 2019. This project is no longer supported and due to sunsetting of Bintray the binary builds are no longer available. The best strategy at this point would be to migrate to a supported version of Elasticsearch and another plugin such as Hunspell.

h1. Morphological Analysis Plugin for Elasticsearch

The Morphological Analysis plugin integrates <a href="https://github.com/AKuznetsov/russianmorphology">Russian and English morphology for java and lucene framework</a> into elasticsearch. This plugin adds two new analyzers: "russian_morphology" and "english_morphology" and two token filters with the same names.

The <a href="https://github.com/imotov/elasticsearch-analysis-morphology/blob/master/demo.sh">demo.sh</a> file shows a few examples of the analyzers behavior.

h2. Switching to Hunspell

*NOTE:* Please note that this plugin is available only for Elasticsearch v5.6.x and below. For Elasticsearch version 6.0 and above consider switching to the officially supported <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/analysis-hunspell-tokenfilter.html">hunspell</a> token filter with russian dictionaries.

h2. Building from the source

In order to build the project from the source, you need *git*, *maven* and *java JDK* 8 and follow a few simple steps:

First you need to build the last commit of russianmorphology that supported Lucene 6.x:

bc. $ git clone https://github.com/AKuznetsov/russianmorphology.git
$ cd russianmorphology
$ git checkout 6fc7e109cb23c88cfb44313275df44117b8b97f7
$ mvn install

If it resulted in the following output, we can move on. 

bc. [INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------


Now we can check out *5.6* branch of this project and build it using the russianmorphology library that we built in previous step.

bc. $ cd ..
$ git clone https://github.com/imotov/elasticsearch-analysis-morphology.git
$ cd elasticsearch-analysis-morphology
$ git checkout 5.6
$ ./gradlew assemble -Drepos.mavenlocal=true

If you see the following message, the build was successful:

bc. BUILD SUCCESSFUL in 26s
11 actionable tasks: 11 executed

The plugin can be found file is @build/distributions/analysis-morphology-5.6.16.zip@




