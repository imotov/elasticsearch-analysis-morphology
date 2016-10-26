#!/bin/sh
curl -XDELETE 'http://localhost:9200/rustest' && echo
curl -XPUT 'http://localhost:9200/rustest' -d '{
    "settings": {
		"analysis": {
			"analyzer": {
				"my_analyzer": {
					"type": "custom",
					"tokenizer": "standard",
					"filter": ["lowercase", "russian_morphology", "english_morphology", "my_stopwords"]
				}
			},
			"filter": {
				"my_stopwords": {
					"type": "stop",
					"stopwords": "а,без,более,бы,был,была,были,было,быть,в,вам,вас,весь,во,вот,все,всего,всех,вы,где,да,даже,для,до,его,ее,если,есть,еще,же,за,здесь,и,из,или,им,их,к,как,ко,когда,кто,ли,либо,мне,может,мы,на,надо,наш,не,него,нее,нет,ни,них,но,ну,о,об,однако,он,она,они,оно,от,очень,по,под,при,с,со,так,также,такой,там,те,тем,то,того,тоже,той,только,том,ты,у,уже,хотя,чего,чей,чем,что,чтобы,чье,чья,эта,эти,это,я,a,an,and,are,as,at,be,but,by,for,if,in,into,is,it,no,not,of,on,or,such,that,the,their,then,there,these,they,this,to,was,will,with"
				}
			}
		}
	}
}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/_mapping' -d '{
	"type1": {
	    "_all" : {"analyzer" : "russian_morphology"},
    	"properties" : {
        	"body" : { "type" : "string", "analyzer" : "russian_morphology" }
    	}
	}
}' && echo
curl -XPUT 'http://localhost:9200/rustest/type2/_mapping' -d '{
	"type2": {
        "_all" : {"analyzer" : "russian_morphology"},
    	"properties" : {
        	"text" : { "type" : "string", "analyzer" : "my_analyzer" }
    	}
	}
}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/1' -d '{"body": "У московского бизнесмена из автомобиля украли шесть миллионов рублей "}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/2' -d '{"body": "Креативное агентство Jvision, запустило сервис, способствующий развитию автомобильного туризма в России."}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/3' -d '{"body": "Просто авто"}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/4' -d '{"body": "Японские автомобили вновь заняли в США первые места в рейтингах"}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/5' -d '{"body": "Январский дефицит платежного баланса Японии превысил $5 млрд"}' && echo
curl -XPUT 'http://localhost:9200/rustest/type1/6' -d '{"body": "Японская корпорация Sony представила новый смартфон под названием Xperia Sola."}' && echo
curl -XPOST 'http://localhost:9200/rustest/_refresh' && echo
echo "Should return 5"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:Япония"}}}'  | grep "_id"
echo "Should return 4, 6"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:Японский"}}}'  | grep "_id"
echo "Should return 4"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:первый"}}}'  | grep "_id"
echo "Should return 1, 4"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:автомобиль"}}}'  | grep "_id"
echo "Should return 2"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:автомобильный"}}}'  | grep "_id"
echo "Should return 3"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:авто"}}}'  | grep "_id"
echo "Should return 1,2,3,4"
curl -s 'http://localhost:9200/rustest/type1/_search?pretty=true' -d '{"query": {"query_string": {"query": "body:авто*", "analyze_wildcard": true}}}'  | grep "_id"

curl -XPUT 'http://localhost:9200/rustest/type2/1' -d '{"text": "Curiously enough, the only thing that went through the mind of the bowl of petunias as it fell was Oh no, not again."}' && echo
curl -XPUT 'http://localhost:9200/rustest/type2/2' -d '{"text": "Many people have speculated that if we knew exactly why the bowl of petunias had thought that we would know a lot more about the nature of the Universe than we do now."}' && echo
curl -XPUT 'http://localhost:9200/rustest/type2/3' -d '{"text": "Не повезло только кашалоту, который внезапно возник из небытия в нескольких милях над поверхностью планеты."}' && echo
curl -XPOST 'http://localhost:9200/rustest/_refresh' && echo
echo "Should return 3"
curl -s 'http://localhost:9200/rustest/type2/_search?pretty=true' -d '{"query": {"query_string": {"query": "text:\"миль по поверхности\"", "analyze_wildcard": true}}}'  | grep "_id"
echo "Should return 1"
curl -s 'http://localhost:9200/rustest/type2/_search?pretty=true' -d '{"query": {"query_string": {"query": "text:go", "analyze_wildcard": true}}}'  | grep "_id"
echo "Should return 2"
curl -s 'http://localhost:9200/rustest/type2/_search?pretty=true' -d '{"query": {"query_string": {"query": "text:thinking", "analyze_wildcard": true}}}'  | grep "_id"
echo "Searching _all field"
curl -XPOST 'localhost:9200/rustest/_search?pretty' -d '{
  "query": { 
      "query_string": { 
          "query": "Япония",
          "analyze_wildcard": true
      } 
  }
}'