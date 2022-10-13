import json
import wn

'''
With this class you can install language models - used in the request of dictionaries.
Check the names of the models with in the file config_lang.json and the following model documentations:
    * Open Wordnet:
        * https://pypi.org/project/wn/
        * https://github.com/goodmami/wn
'''


class InstallLanguages:

    def __init__(self, config_lang):
        self.wordnet_languages = config_lang['wordnet_languages']

    def install_semantic_relations_wn_language_model(self, lang):
        if lang in self.wordnet_languages.keys():
            wn.download(self.wordnet_languages[lang])
            return 0
        else:
            print(lang + ' is not available in the Open Wordnet (wn) modules.')
            return -1


if __name__ == '__main__':

    conf = json.load(open('config_lang.json'))

    inst_lang = InstallLanguages(conf)
    inst_lang.install_semantic_relations_wn_language_model('de')
