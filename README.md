# ShopRulesGenerator
ShopRulesGenerator is a microservice component, that produces shop specific rules. 
[The Parser](https://github.com/HPI-BP2017N2/Parser) uses the rules to extract product details from HTML. 
The microservice is written in Java and uses the Spring framework.


## Getting started
### Prerequisites

To run the microservice it is required to set up the following:

1. MongoDB
...The MongoDB is used to store generated shop rules (in the collection named 'shopRules').

2. Idealo bridge
...The ShopRulesGenerator uses idealo offers to analyse the structure of shop product pages. 
The idealo bridge provides these to the ShopRulesGenerator. 
Furthermore the resolving from shopID to shop root url is used to remove the root url from image links.

3. [URL-Cleaner microservice](https://github.com/HPI-BP2017N2/URLCleaner)
...Since the urls of the idealo offers can contain click trackers, it is required to clean them before visiting.

### Configuration

The name of the database which will be used is 'data'.

#### Environment variables
- SHOPRULESGENERATOR_PORT: The port that should be used by the ShopRulesGenerator.
- MONGO_IP: The IP of the MongoDB instance.
- MONGO_PORT: The port of the MongoDB instance.
- MONGO_BRIDGE_USER: The username to access the MongoDB.
- MONGO_BRIDGE_PW: The password to access the MongoDB.
- CLIENT_ID: The OAuth2 client ID for accessing the Idealo bridge.
- CLIENT_SECRET: The OAuth2 client secret ID for accessing the Idealo bridge.
- ACCESS_TOKEN_URI: The uri which will be used to get an authorization token for the Idealo bridge.
- API_URL: The root url of the Idealo API.
- URLCLEANER_IP: The root url of the URLCleaner (including port if required).

#### Component properties
- maxOffers: Amount of sample offers that will be used for analysing the structure of shop product pages.
- fetchDelay: Time between fetching of HTML pages corresponding to sample offers.
- userAgent: User agent used during fetching of HTML pages.
- scoreThreshold: All selectors with a normalized score below threshold will be dropped and not stored in database.
- corePoolSize: Amount of CPU Cores which should be used during generation.
- maxPoolSize: Max amount of running threads at the same time.
- queueCapacity: Max amount of waiting tasks to ShopRulesGenerator.

## How it works

1. The ShopRulesGenerator (SRG) receives a request, to get the rules for a specific shop identified by the internal 
Idealo ID)
2. If the rules are already existing in the DB, the SRG returns them. If they do not exists, it will trigger the 
generations process. It is safe to call the SRG multiple times within in the same time with the same shop. The 
generation process will get only triggered once.
3. During the generation process the shop rules generator will load *maxOffers* sample offers from the Idealo bridge.
...For each offer the SRG will:
3.1. Fetch the html page using the cleaned url of the sample offer
3.2. Do some pre-processing with the image urls and price
3.3  Build the selectors using the offer attributes and the loaded HTML page.
4. A score gets calculated for every generated selector by comparing how often the selector findings have matched 
the value from the idealo offer.
5. The rules get stored.

### Types of selectors

There are three types of selectors.
1. TextNode: This describes nodes where the wanted content is contained within a tag.
```html <span key='value'>wanted value</span>```
2. AttributeNode: This describes nodes, where the wanted content is contained within the tag description.
```html <span key='wanted value'>not interesting</span>```
3. DateNode: These ones are script tags, where the content is contained within JSON.
```html <script>function c() { let products: { 'ean': 'wanted value'}}</script>```
...The DataNode consists of a cssSelector, a block path and a jsonPath. 
...A block is specified as a text which is surrounded by { and }.
...The css selector helps finding the correct script tag.
...The block path helps do navigate through javascript, to find the correct block.
...The json path is used after parsing the found block as json to get the wanted value.

### Further information

To get a better understanding, play a bit with [JSoup](https://jsoup.org/) and [JsonPath](https://github.com/json-path/JsonPath)
JSoup is used for parsing CSS-Selectors and JsonPath is used to navigate through JsonObjects.

## Outlook
- Let the shop rules expire after a specific TTL.
- Investigate how to let JSoup handle quotes within attribute names.
- Make an improvement, where the price we want to look up (like $12.00) gets converted to ($12.0) since a few shops 
use this format in their script tags.
- Implement a better strategy for loading sample offers and think about making the selectors depend on category.
- Apply DataNodeSelector - generation not only to script tags, instead take a look on "onclick" methods in <a> - tags too.
