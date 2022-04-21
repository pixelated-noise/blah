# Notes on simpleNLG

## Elements

- Document - "TextElements can be structured in a tree-like structure where
  elements can contain child components. These child components can in turn
  contain other child components and so on. There are restrictions on the type
  of child components a particular element type can have."
- String - canned text
- List - see below
- Word - An entry from the lexicon (obtained via lookup), also has features.
- InflectedWord - A word that requires inflection by the morphology.
- CoordinatedPhrase - defines coordination between two or more
  phrases. Coordination involves the linking of phrases together through the use
  of key words such as "and" or "but".
- Phrases - see subclasses of `PhraseElement` but also `PhraseCategory`
  - SP or Clause - does a lot of work to build other phrases for you
  - Adjective phrase
  - Adverb phrase
  - NP - noun phrase
  - PP - prepositional phrase
  - VP - verb phrase
  - Canned text

From javadoc:

> The list element itself adds no additional meaning to the realisation. For
> example, the syntax processor takes a phrase element and produces a list
> element containing inflected word elements. Phrase elements only have meaning
> within the syntax processing while the morphology processor (the next in the
> sequence) needs to work with inflected words. Using the list element helps to
> keep the inflected word elements together.

## NLGModules

All processing modules perform realisation on a tree of `NLGElement`s. The
modules can alter the tree in whichever way they wish. For example, the syntax
processor replaces phrase elements with list elements consisting of inflected
words while the morphology processor replaces inflected words with string
elements.

Order: syntax, morphology, orthography, formatter (text or HTML)

- `SyntaxProcessor` - translates phrases into lists of words

- `MorphologyProcessor` - inflects words form the base form depending on the
  features applied to the word. For example, "kiss" is inflected to "kissed" for
  past tense, "dog" is inflected to "dogs" for pluralisation.

- `OrthographyProcessor`

  This processing module deals with punctuation when applied to
  `DocumentElement`s. The punctuation currently handled by this processor includes
  the following (as of version 4.0):

  - Capitalisation of the first letter in sentences.
  - Termination of sentences with a period if not interrogative.
  - Termination of sentences with a question mark if they are interrogative.
  - Replacement of multiple conjunctions with a comma. For example, John and Peter
    and Simon becomes John, Peter and Simon.

- `TextFormatter`

  - Adding the document title to the beginning of the text.
  - Adding section titles in the relevant places.
  - Adding appropriate new line breaks for ease-of-reading.
  - Adding list items with ' * '.
  - Adding numbers for enumerated lists (e.g., "1.1 - ", "1.2 - ", etc.)

## Implementation criticism

- The subclass of `PhraseElement` but also `PhraseCategory` both determine the
  different types of phrase, and their names are inconsistent
- Element constructors do a lot of work to help the user construct the tree
  easily, for example SPhraseSpec.addModifier() uses heuristics to decide where
  it goes (pre- or post-). It's like the coercers that data macros use.
