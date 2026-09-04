# DataModelTranslationWrapper for Arrowhead 5

This project contains two Application systems providing the following functionalities:
1. *DataModelTranslationWrapper*: Provides a dataModelTranslation service, where the translation is performed using python scripts from an outer source.
2. *DataSaver*: Saves the input data into a configured directory. Useful for testing.

## DataModelTranslationWrapper Provider

This wrapper provider makes data model translators - developed in python - [Arrowhead 5 compliant](https://aitia-iiot.github.io/ah5-docs-java-spring/home/welcome/). It exposes a python solution as a [dataModelTranslation](https://aitia-iiot.github.io/ah5-docs-java-spring/tools_assets/translation/translation_provider_dev/#data-model-translation-provider) service instance.

### Configuration

The `application.properties` configuration file is located next to the executable `jar` file.

#### Configure the translator

**model.ids**<br />
List here the model identifiers, that the translator supports.

**result.mime.types**<br />
List here the the result mime types that the translator produce.

**python.launcher.path**<br />
The absolute path to python. (Could be different from the main if python environments are used).

**init.script.location**<br />
The absolute path to the initialization python script. The wapper registers only to Arrowhead when this script is done.

**translation.script.location**<br />
The absolute path to the translation python script.

**translation.script.input.folder**<br />
The absolute path to the input folder that the translation python script is reading for incoming inputs.

**translation.script.output.folder**<br />
The absolute path to the output folder that the wrapper reads for the translation results.

## Public Data Model Translation scripts

### SemanticAI Translator for IPC-2581

- [Download from here](https://github.com/Aitia-IIOT/ah-ai-translation-poc/tree/master/IPC_translate/v2)
- Review its ReadMe
- Run `pip install -r requirements.txt` to install the dependecies
- Adjust your api keys (see ReadMe)
- Configure the DataModelTranslationWrapper to use this translator.

