# Create German dictionaries by different German semantic resources

The code of this repository is able to create dictionaries of *synonyms*, *hyponyms* and *hypernyms* of a given
word list (text file [input/word_list.txt](input/word_list.txt)):

```text
Fall
fallen
gefallen
Unfall
Absturz
Sturz
Bruch
brechen
gebrochen
Oberschenkel
```

* Input for word lists used in Polar project:
  * [input/delir.txt](input/delir.txt)
  * [input/sturz.txt](input/sturz.txt)

## Resources

### Open WordNet
* install Open WordNet resources and run script from [install_language.py](install_language.py)

### GermaNet
* request [GermaNet](https://uni-tuebingen.de/fakultaeten/philosophische-fakultaet/fachbereiche/neuphilologie/seminar-fuer-sprachwissenschaft/arbeitsbereiche/allg-sprachwissenschaft-computerlinguistik/ressourcen/lexica/germanet-1/) files
* configure in [params.conf](param.conf) your GermaNet path
* Script configured and tested with GermaNet version 17.0 (_GN_V170_XML_) 
* By usage of GermaNet, do not forget to [cite](https://uni-tuebingen.de/fakultaeten/philosophische-fakultaet/fachbereiche/neuphilologie/seminar-fuer-sprachwissenschaft/arbeitsbereiche/allg-sprachwissenschaft-computerlinguistik/ressourcen/lexica/germanet-1/germanet-zitieren/) it.

### Wiktionary
* Wiktionary items are processed by wiktionary dumps,
e.g.: [current dump](https://dumps.wikimedia.org/dewiktionary/latest/dewiktionary-latest-pages-meta-current.xml.bz2)
    * The wiktionary dump file is processed by an own script in Jan / Feb 2022 by an own separated script.
    * The preprocessed data (json files) from Jan / Feb 2022 is stored in [wiktionray/wiktionary_de_semantic_relations.json](wiktionray/wiktionary_de_semantic_relations.json)
    * Configure `wiktionary_de_semantic_relations.json` in [params.conf](param.conf)

# Usage
## Configuration
* Prepare [input/word_list.txt](input/word_list.txt) with input parameters
* Configure [param.conf](param.conf) before usage with input words: one word == one line
  * _request_soruces_: available modes are _wordnet, wiktionary, germanet_ and _combined_ to use the named resource together
  * If you use GermaNet, configure the path of GermaNet files there

```editorconfig
[input]
path_input_words = input/word_list.txt
request_soruces = wordnet, wiktionary, germanet, combined
path_wiktionary_de = wiktionary/wiktionary_de_semantic_relations.json
path_germanet = /home/chlor/PycharmProjects/feattext/ext_res/germanet/GN_V170/GN_V170_XML

[output]
path_out = out
```
* run `python request_words.py param.conf `


## Output
* `all_values.tsv` with all counted values
* for every resource, a json file and a txt is produced, e.g. output snippets by Wiktionary resource:

```text
term	category
Abstieg	Fall-synonym-wkt
Casus	Fall-synonym-wkt
Kasus	Fall-synonym-wkt
Niedergang	Fall-synonym-wkt
Patient	Fall-synonym-wkt
Bewegung	Fall-hypernym-wkt
Laufendes Gut	Fall-hypernym-wkt
...
rational	gebrochen-hypernym-wkt
Bein	Oberschenkel-hypernym-wkt
Körperteil	Oberschenkel-hypernym-wkt
Schenkel	Oberschenkel-hypernym-wkt
```


```text
{
  "Absturz": {
    "hyponyms": [
      "Bergsteigerabsturz",
      "Bungee-Jumper-Absturz",
      "Flugzeugabsturz",
      "Gondelabsturz",
      "Helikopterabsturz",
      "Hubschrauberabsturz",
      "Klettererabsturz",
      "Meteorabsturz",
      "Meteoritenabsturz",
      "Programmabsturz",
      "Raumfährenabsturz",
      "Satellitenabsturz",
      "UFO-Absturz",
      "Zinsen-Absturz"
    ],
    "hyponyms_len": 14,
    "synonyms": [
      "Computerabsturz"
    ],
    "synonyms_len": 1
  },
  "Bruch": {
    "hypernyms": [
      "Ablösung",
      "Diebstahl",
      "Fraktur",
      "Raub",
      "Rechenoperation",
      "Störung",
      "Symbol",
      "Trennung",
      "Verbrechen",
      "Verstoß",
      "Weinfehler",
      "Zweig",
      "Überrest"
    ],
    "hypernyms_len": 13,
    "hyponyms": [
      "Abbruch",
      "Achsenbruch",
      "Anbruch",
      "Armbruch",
      "Aufbruch",
      "Ausbruch",
      "Beckenbruch",
      "Beinbruch",
      "Brauner Bruch",
      "Dammbruch",
      "Deichbruch",
      "Dezimalbruch",
      "Durchbruch",
      "Ehebruch",
      "Emscherbruch",
      "Erlenbruch",
      "Ermüdungsbruch",
      "Friedensbruch",
      "Gesetzesbruch",
      "Glasbruch",
      "Grabenbruch",
      "Grauer Bruch",
      "Handknochenbruch",
      "Hausfriedensbruch",
      "Kettenbruch",
      "Kieferbruch",
      "Kleidung",
      "Koalitionsbruch",
      "Lebensbruch",
      "Leistenbruch",
      "Mastbruch",
      "Nabelbruch",
      "Oberschenkelhalsbruch",
      "Oderbruch",
      "Radbruch",
      "Rechtsbruch",
      "Rohrbruch",
      "Schiffbruch",
      "Schotbruch",
      "Schwarzer Bruch",
      "Schädelbruch",
      "Steinbruch",
      "Stilbruch",
      "Stimmbruch",
      "Tabubruch",
      "Tarifbruch",
      "Traditionsbruch",
      "Treuebruch",
      "Umbruch",
      "Verfassungsbruch",
      "Vertragsbruch",
      "Vertrauensbruch",
      "Weißer Bruch",
      "Windbruch",
      "Wolkenbruch",
      "Wortbruch",
      "Zehenbruch",
      "Zehnerbruch",
      "Zivilisationsbruch",
      "Zusammenbruch",
      "Zwerchfellbruch"
    ],
    "hyponyms_len": 61,
    "synonyms": [
      "Abfall",
      "Abkehr",
      "Abspaltung",
      "Abteilung",
      "Abtrennung",
      "Ausschuss",
      "Bauschutt",
      "Beinkleid",
      "Bresche",
      "Bruchteil",
      "Bruchzahl",
      "Canyon",
      "Dissens",
      "Distanz",
      "Ehescheidung",
      "Einbruch",
      "Einschnitt",
      "Entzweiung",
      "Fahrlässigkeit",
      "Falte",
      "Falz",
      "Fehler",
      "Fehltritt",
      "Fehlverhalten",
      "Feindschaft",
      "Feindseligkeit",
      "Fraktur",
      "Fuge",
      "Furche",
      "Gezänk",
      "Hader",
      "Hass",
      "Hernie",
      "Hose",
      "Händel",
      "Kehricht",
      "Klamm",
      "Knacks",
      "Knick",
      "Kniff",
      "Knitter",
      "Knochenbruch",
      "Krach",
      "Leck",
      "Lockerung",
      "Lossagung",
      "Luch",
      "Lösung",
      "Lücke",
      "Marsch",
      "Marschland",
      "Meinungsverschiedenheit",
      "Moor",
      "Morast",
      "Mulde",
      "Narbe",
      "Nichtbeachtung",
      "Nichtbefolgung",
      "Nichterfüllung",
      "Panne",
      "Pass",
      "Pflichtvergessenheit",
      "Plunder",
      "Quotient",
      "Ramsch",
      "Renitenz",
      "Restmüll",
      "Riss",
      "Ritze",
      "Sabotage",
      "Sachbeschädigung",
      "Sachschaden",
      "Saumseligkeit",
      "Scheidung",
      "Schisma",
      "Schleuderware",
      "Spalt",
      "Spalte",
      "Spaltung",
      "Sprung",
      "Streit",
      "Sumpf",
      "Trennung",
      "Umschlag",
      "Umwälzung",
      "Ungehorsam",
      "Unterbrechung",
      "Unterlassung",
      "Untreue",
      "Verfehlung",
      "Verfeindung",
      "Verletzung",
      "Versäumnis",
      "Verwerfung",
      "Widerspenstigkeit",
      "Zerbrochenes",
      "Zerwürfnis",
      "Zuwiderhandlung",
      "Zwischenraum",
      "Zäsur",
      "rationale Zahl",
      "Übertretung"
    ],
    "synonyms_len": 102
  }
}
```