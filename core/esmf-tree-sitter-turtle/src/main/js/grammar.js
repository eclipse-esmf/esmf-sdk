// [X]  See section "6.5 Grammar" in https://www.w3.org/TR/turtle/#sec-grammar-grammar for
//      corresponding rule x.

// [26]
const UCHAR = /(\\u[0-9A-Fa-f]{4}|\\U[0-9A-Fa-f]{8})/

// [154s]
const EXPONENT = [
  /[eE]/,
  /[+-]?/,
  /\d+/
]

// [161s]
const WS = [
  /\x20/,
  /\x09/,
  /\x0D/,
  /\x0A/
]

// [163s]
const PN_CHARS_BASE = [
  /[A-Z]/,
  /[a-z]/,
  /[\u00C0-\u00D6]/,
  /[\u00D8-\u00F6]/,
  /[\u00F8-\u02FF]/,
  /[\u0370-\u037D]/,
  /[\u037F-\u1FFF]/,
  /[\u200C-\u200D]/,
  /[\u2070-\u218F]/,
  /[\u2C00-\u2FEF]/,
  /[\u3001-\uD7FF]/,
  /[\uF900-\uFDCF]/,
  /[\uFDF0-\uFFFD]/,
  /[\u{10000}-\u{EFFFF}]/u
]

// [164s]
const PN_CHARS_U = PN_CHARS_BASE.concat('_')

// [166s]
const PN_CHARS = PN_CHARS_U.concat([
  '-',
  /\d/,
  /[\u00B7]/,
  /[\u0300-\u036F]/,
  /[\u203F-\u2040]/
])

// [171s]
const HEX = [
  /\d/,
  /[A-F]/,
  /[a-f]/
]

// [172s]
const PN_LOCAL_ESC = [
  '_',
  '~',
  '.',
  '-',
  '!',
  '$',
  '&',
  "'",
  '(',
  ')',
  '*',
  '+',
  ',',
  ';',
  '=',
  '/',
  '?',
  '#',
  '@',
  '%'
].map(char => '\\' + char)

String.prototype.toCaseInsensitive = function() {
  return alias(
    token(new RegExp(
      this
        .split('')
        .map(letter => `[${letter}${letter.toLowerCase()}]`)
        .join('')
    )),
    this
  )
}

module.exports = grammar({
  name: 'turtle',

  extras: $ => [
    $.comment,
    /\s/
  ],

  word: $ => $.pn_prefix,

  rules: {

    // [1g] trigDoc ::= (directive | block)*
    document: $ => repeat(choice($.directive, $.triple, $.graph)),

    // wrappedGraph ::= '{' triplesBlock? '}'
    // NOTE the last wrappedGraph triple does not have to terminate in a '.'
    graph: $ => seq(
      optional(field('label', $._label)),
      '{',
      seq(
        repeat($.triple),
        optional($._triples),
      ),
      '}',
    ),

    // [7g] labelOrSubject ::= iri | BlankNode
    _label: $ => seq(optional('GRAPH'), choice($._iri, $._blank_node)),

    comment: _ => token(prec(-1, /#.*/)),

    // [2]
    // Triple Statement vs Graph Statement:
    // https://www.w3.org/TR/trig/#sec-triple-statements
    triple: $ => seq($._triples, '.'),

    // [3]
    directive: $ => choice(
      $.prefix_id,
      $.base,
      $.sparql_prefix,
      $.sparql_base
    ),

    // [4]
    prefix_id: $ => seq(
      '@prefix',
      $.namespace,
      $.iri_reference,
      '.'
    ),

    // [5]
    base: $ => seq(
      '@base',
      $.iri_reference,
      '.'
    ),

    // [5s]
    sparql_base: $ => seq(
      'BASE'.toCaseInsensitive(),
      $.iri_reference,
    ),

    // [6s]
    sparql_prefix: $ => seq(
      'PREFIX'.toCaseInsensitive(),
      $.namespace,
      $.iri_reference,
    ),

    // [6]
    _triples: $ => choice(
      seq(
        $.subject,
        $.property_list
      ),
      seq(
        $.blank_node_property_list,
        optional($.property_list)
      )
    ),

    // [7]
    property_list: $ => seq(
      $.property,
      repeat(seq(
        ';',
        optional($.property)
      ))
    ),

    // Enable incremental selection of properties
    property: $ => seq(
      $.predicate,
      $.object_list,
    ),

    // [8]
    object_list: $ => seq(
      $._object,
      repeat(seq(
        ',',
        $._object
      ))
    ),

    // [9]
    // [11]
    predicate: $ => choice(
      $._iri,
      'a'
    ),

    // [10]
    subject: $ => choice(
      $._iri,
      $._blank_node,
      $.collection
    ),

    // [12]
    _object: $ => choice(
      $._iri,
      $._blank_node,
      $.collection,
      $.blank_node_property_list,
      $._literal
    ),

    // [13]
    _literal: $ => choice(
      $.rdf_literal,
      $._numeric_literal,
      $.boolean_literal
    ),

    // [14]
    blank_node_property_list: $ => seq(
      '[',
      $.property_list,
      ']'
    ),

    // [15]
    collection: $ => seq(
      '(',
      optional($.object_collection),
      ')'
    ),

    // Enable incremental selection analog to blank_node_property_list
    object_collection: $ => repeat1(
      $._object,
    ),

    // [16]
    _numeric_literal: $ => choice(
      $.integer,
      $.decimal,
      $.double
    ),

    // [17]
    string: $ => choice(
      $._string_literal_quote,
      $._string_literal_single_quote,
      $._string_literal_long_quote,
      $._string_literal_long_single_quote,
    ),

    // [18]
    iri_reference: _ =>
      seq(
        '<',
        // expose <#leading> anchor for syntax highlighting
        optional(token(prec(1, '#'))),
        token.immediate(
          repeat(choice(/([^<>"{}|^`\\\x00-\x20])/, UCHAR)),
        ),

        token.immediate('>'),
      ),

    // [19]
    integer: _ => token(/[+-]?\d+/),

    // [20]
    decimal: _ => token(seq(/[+-]?/, /\d*/, '.', /\d+/)),

    // [21]
    double: _ => token(seq(
      /[+-]?/,
      choice(
        seq(/\d+/, '.', /\d*/, seq(...EXPONENT)),
        seq('.', /\d+/, seq(...EXPONENT)),
        seq(/\d+/, seq(...EXPONENT))
      ))
    ),

    // [22]
    _string_literal_quote: $ => seq(
      '"',
      repeat(choice(
        /[^\x22\x5C\x0A\x0D]/,
        $.echar,
        UCHAR
      )),
      '"',
    ),

    // [23]
    _string_literal_single_quote: $ => seq(
      "'",
      repeat(choice(
        /[^\x27\x5C\x0A\x0D]/,
        $.echar,
        UCHAR
      )),
      "'"
    ),

    // [24]
    _string_literal_long_single_quote: $ => seq(
      "'''",
      repeat(seq(
        optional(choice(
          "'",
          "''",
        )),
        choice(
          /[^'\\]/,
          $.echar,
          UCHAR
        )
      )),
      "'''",
    ),

    // [25]
    _string_literal_long_quote: $ => seq(
      '"""',
      repeat(seq(
        optional(choice(
          '"',
          '""',
        )),
        choice(
          /[^"\\]/,
          $.echar,
          UCHAR
        )
      )),
      '"""',
    ),

    // [128s]
    rdf_literal: $ => seq(
      field('value', $.string),
      optional(choice(
        $.lang_tag,
        field('datatype', seq('^^', $._iri))
      ))
    ),

    // [133s]
    boolean_literal: _ => choice(
      'true',
      'false'
    ),

    // [135s]
    _iri: $ => choice(
      $.iri_reference,
      $.prefixed_name
    ),

    // [136s]
    prefixed_name: $ => seq(
      $.namespace,
      optional($.pn_local)
    ),

    // [137s]
    _blank_node: $ => choice(
      $.blank_node_label,
      $.anon
    ),

    // [139s]
    namespace: $ => seq(
      optional($.pn_prefix),
      ':'
    ),

    // [141s]
    blank_node_label: _ => seq(
      '_:',
      token.immediate(seq(
        choice(
          ...PN_CHARS_U,
          /\d/
        ),
        optional(seq(
          repeat(choice(
            ...PN_CHARS,
            '.'
          )),
          choice(...PN_CHARS)
        ))
      ))
    ),

    // [144s]
    lang_tag: _ => token(seq(
      '@',
      /[a-zA-Z]+/,
      repeat(seq('-', /[a-zA-Z0-9]+/))
    )),

    // [159s]
    echar: _ => /\\[tbnrf\\"']/,

    // [162s]
    anon: _ => token(seq(
      '[',
      repeat(choice(...WS)),
      ']'
    )),

    // [167s]
    pn_prefix: _ => token(seq(
      choice(...PN_CHARS_BASE),
      optional(seq(
        repeat(choice(
          ...PN_CHARS,
          '.'
        )),
        choice(...PN_CHARS)
      ))
    )),

    // [168s]
    pn_local: _ => token.immediate(seq(
      choice(
        ...PN_CHARS_U,
        ':',
        /\d/,
        seq('%', choice(...HEX), choice(...HEX)),
        ...PN_LOCAL_ESC
      ),
      optional(seq(
        repeat(choice(
          ...PN_CHARS,
          '.',
          ':',
          seq('%', choice(...HEX), choice(...HEX)),
          ...PN_LOCAL_ESC
        )),
        choice(
          ...PN_CHARS,
          ':',
          seq('%', choice(...HEX), choice(...HEX)),
          ...PN_LOCAL_ESC
        )
      ))
    )),

  }
})
