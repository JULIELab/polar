import argparse
import csv
import json
import os

import pandas as pd
import wn
import configparser
import itertools
from germanetpy import germanet


class RequestWordsDE:

    def __init__(self, config):

        self.path_input_words = config['input']['path_input_words']
        self.request_soruces = config['input']['request_soruces']

        if 'wordnet' in self.request_soruces:
            self.wordnet_object = wn.Wordnet("odenet:1.4")

        if 'wiktionary' in self.request_soruces:
            self.path_wiktionary_de = config['input']['path_wiktionary_de']
            with open(self.path_wiktionary_de) as json_file:
                self.wiktionary_object = json.load(json_file)

        if 'germanet' in self.request_soruces:
            self.germanet_object = germanet.Germanet(config['input']['path_germanet'])

        self.words = [w.strip() for w in open(self.path_input_words)]

        output_pat = config['output']['path_out']
        if not os.path.isdir(output_pat):
            os.mkdir(output_pat)

        self.path_out_german_wordnet = output_pat + os.sep + 'german_wordnet' + '.json'
        self.path_out_german_wiktionary = output_pat + os.sep + 'german_wiktionary' + '.json'
        self.path_out_germanet = output_pat + os.sep + 'germanet' + '.json'

        self.path_out_german_wordnet_txt = output_pat + os.sep + 'german_wordnet_txt' + '.txt'
        self.path_out_german_wiktionary_txt = output_pat + os.sep + 'german_wiktionary_txt' + '.txt'
        self.path_out_germanet_txt = output_pat + os.sep + 'germanet_txt' + '.txt'
        self.path_out_all_tsv = output_pat + os.sep + 'all_tsv' + '.tsv'

        self.path_out_combined = output_pat + os.sep + 'combined'

        self.dict_combined = {}
        self.counts = {}

    def write_json_out_file(self, content_dict, out_json_file):
        with open(out_json_file, "w", encoding="utf-8") as outfile:
            json.dump(
                content_dict,
                outfile,
                indent=2,
                ensure_ascii=False,
                sort_keys=True
            )

    def write_txt_out_file(self, content_dict, out_txt_file, res):

        ents = ['term\tcategory']
        #ent_dict = {'term': 'category'}
        ent_dict = {}

        for el in content_dict:
            if 'synonyms' in content_dict[el].keys():
                for x in content_dict[el]['synonyms']:
                    ents.append(x + '\t' + el + '-synonym-' + res)
                    ent_dict[x] = el + '-synonym-' + res
            if 'hypernyms' in content_dict[el].keys():
                for x in content_dict[el]['hypernyms']:
                    ents.append(x + '\t' + el + '-hypernym-' + res)
                    ent_dict[x] = el + '-hypernym-' + res
            if 'hyponyms' in content_dict[el].keys():
                for x in content_dict[el]['hyponyms']:
                    ents.append(x + '\t' + el + '-hyponym-' + res)
                    ent_dict[x] = el + '-hyponym-' + res

        f = open(out_txt_file, "w")
        f.write("\n". join(ents))
        f.close()
        return ent_dict


    def request_german_wordnet(self, word):
        synonyms = set()
        hypernyms = set()
        hyponyms = set()

        for synset in self.wordnet_object.synsets(word):
            synos = set(synset.lemmas())

            if word in synos:
                synos.remove(word)
            synonyms.update(synos)

            hypo = synset.hyponyms()
            hyper = synset.hypernyms()

            if hyper:
                hypernyms.update(list(itertools.chain(*[syn.lemmas() for syn in hyper])))
            if hypo:
                hyponyms.update(list(itertools.chain(*[syn.lemmas() for syn in hypo])))

        return synonyms, hypernyms, hyponyms

    def request_german_wiktionary(self, word):
        synonyms = set()
        hypernyms = set()
        hyponyms = set()

        if 'synonyms' in self.wiktionary_object[word].keys():
            synonyms = set(self.wiktionary_object[word]['synonyms'])

        if 'hypernyms' in self.wiktionary_object[word].keys():
            hypernyms = set(self.wiktionary_object[word]['hypernyms'])

        if 'hyponyms' in self.wiktionary_object[word].keys():
            hyponyms = set(self.wiktionary_object[word]['hyponyms'])

        return synonyms, hypernyms, hyponyms

    def _flat_double_list(self, list_elements):
        flat_list = set(itertools.chain.from_iterable([list_item for list_item in list_elements]))
        return set(value for value in flat_list if not value == 'GNROOT')

    def _flat_triple_list(self, list_elements):
        flat_list = list(itertools.chain.from_iterable([item for list_item in list_elements for item in list_item]))
        return set(value for value in flat_list if not value == 'GNROOT')

    def request_germanet(self, word):
        synset_list = self.germanet_object.get_synsets_by_orthform(word)

        synonyms = self._flat_double_list([[lex.orthform for lex in syn.lexunits] for syn in synset_list])
        if word in synonyms:
            synonyms.remove(word)

        hypernyms = self._flat_triple_list([[[lex.orthform for lex in hyp.lexunits] for hyp in syn.all_hypernyms()] for syn in synset_list])

        hyponyms = []
        if len(synset_list) > 0:
            for syn in synset_list:
                for relax, synies in syn.relations.items():
                    for syni in synies:
                        for _ in syni.lexunits:
                            rel = str(relax).replace('ConRel.', '')

                            if 'hyponym' in rel:
                                for relation_synset in syn.relations[relax]:
                                    for rel_lex_unit in relation_synset.lexunits:
                                        hyponyms.append(rel_lex_unit.orthform)

        return set(synonyms), set(hypernyms), set(hyponyms)


    def create_dict_entry(self, synonyms, hypernyms, hyponyms, w, res):
        res_dict = {}

        if synonyms:
            res_dict['synonyms'] = sorted(list(synonyms))
            res_dict['synonyms_len'] = len(list(synonyms))
            if 'synonyms' not in self.dict_combined[w].keys():
                self.dict_combined[w]['synonyms'] = synonyms
            else:
                self.dict_combined[w]['synonyms'].update(synonyms)

        if hypernyms:
            res_dict['hypernyms'] = sorted(list(hypernyms))
            res_dict['hypernyms_len'] = len(list(hypernyms))
            if 'hypernyms' not in self.dict_combined[w].keys():
                self.dict_combined[w]['hypernyms'] = hypernyms
            else:
                self.dict_combined[w]['hypernyms'].update(hypernyms)

        if hyponyms:
            res_dict['hyponyms'] = sorted(list(hyponyms))
            res_dict['hyponyms_len'] = len(list(hyponyms))
            if 'hyponyms' not in self.dict_combined[w].keys():
                self.dict_combined[w]['hyponyms'] = hyponyms
            else:
                self.dict_combined[w]['hyponyms'].update(hyponyms)

        self.counts[w]['synonyms'][res] = len(synonyms)
        self.counts[w]['hypernyms'][res] = len(hypernyms)
        self.counts[w]['hyponyms'][res] = len(hyponyms)

        return res_dict

    def request_german_resources(self):
        wordnet_res = {}
        wiktionary_res = {}
        germanet_res = {}

        print('Resources:', self.request_soruces)

        for w in self.words:
            self.dict_combined[w] = {}
            self.counts[w] = {}
            self.counts[w]['synonyms'] = {}
            self.counts[w]['hypernyms'] = {}
            self.counts[w]['hyponyms'] = {}

            # Open German Wordnet Part
            if 'wordnet' in self.request_soruces:
                wn_synonyms, wn_hypernyms, wn_hyponyms = self.request_german_wordnet(w)
                wordnet_res[w] = self.create_dict_entry(wn_synonyms, wn_hypernyms, wn_hyponyms, w, 'wordnet')

            # Wiktionary Part
            if 'wiktionary' in self.request_soruces:
                if w in self.wiktionary_object.keys():
                    wk_synonyms, wk_hypernyms, wk_hyponyms = self.request_german_wiktionary(w)
                    wiktionary_res[w] = self.create_dict_entry(wk_synonyms, wk_hypernyms, wk_hyponyms, w, 'wiktionary')

            # Germanet Part
            if 'germanet' in self.request_soruces:
                gm_synonyms, gm_hypernyms, gm_hyponyms = self.request_germanet(w)
                germanet_res[w] = self.create_dict_entry(gm_synonyms, gm_hypernyms, gm_hyponyms, w, 'germanet')

            self.dict_combined[w] = dict(self.dict_combined[w])

        txt_dict = {}

        if 'wordnet' in self.request_soruces:
            txt_dict['wordnet'] = self.write_txt_out_file(wordnet_res, self.path_out_german_wordnet_txt, 'wn')
            self.write_json_out_file(wordnet_res, self.path_out_german_wordnet)
            print('German WordNet output in files', self.path_out_german_wordnet, 'and', self.path_out_german_wordnet_txt)
        else:
            print('Wordnet is not requested.')

        if 'wiktionary' in self.request_soruces:
            txt_dict['wiktionary'] = self.write_txt_out_file(wiktionary_res, self.path_out_german_wiktionary_txt, 'wkt')
            self.write_json_out_file(wiktionary_res, self.path_out_german_wiktionary)
            print('German Wiktionary output in file', self.path_out_german_wiktionary, 'and', self.path_out_german_wiktionary_txt)
        else:
            print('Wiktionary is not requested.')

        if 'germanet' in self.request_soruces:
            txt_dict['germanet'] = self.write_txt_out_file(germanet_res, self.path_out_germanet_txt, 'gmn')
            self.write_json_out_file(germanet_res, self.path_out_germanet)
            print('Germanet output in file', self.path_out_germanet, 'and', self.path_out_germanet_txt)
        else:
            print('GermaNet is not requested.')

        out_txt_dict = {}
        for d in txt_dict:
            for e in txt_dict[d]:
                if e not in out_txt_dict.keys():
                    out_txt_dict[e] = txt_dict[d][e]
                else:
                    temp = out_txt_dict[e]
                    out_txt_dict[e] = temp + '|' + txt_dict[d][e]
        with open(self.path_out_all_tsv, 'w', newline='') as csvfile:
            writer = csv.DictWriter(csvfile, ['term', 'category'], delimiter='\t')
            writer.writeheader()
            for entry in out_txt_dict:
                writer.writerow({'term': entry, 'category': out_txt_dict[entry]})

        if 'combined' in self.request_soruces:
            d_combined = {}
            for ent in self.dict_combined:
                d_combined[ent] = {}
                for cat in self.dict_combined[ent]:
                    d_combined[ent][cat] = sorted(list(self.dict_combined[ent][cat]))
                    d_combined[ent][cat+'_cnt'] = len(list(self.dict_combined[ent][cat]))

            self.write_json_out_file(d_combined, self.path_out_combined)
            print('Output of combined resources in file', self.path_out_combined)

            for c in self.counts:
                print('------------------------------------------')
                print(c)
                print()
                df = pd.DataFrame(self.counts[c])
                print(df)

                print('------------------------------------------')


if __name__ == '__main__':

    parser = argparse.ArgumentParser()
    parser.add_argument('conf')
    args = parser.parse_args()

    config = configparser.ConfigParser()
    config.read(args.conf)

    req_w = RequestWordsDE(config)
    req_w.request_german_resources()
