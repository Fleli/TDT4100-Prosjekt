
**Nye feautures**
*Compiler/Språk*
- Nøkkelordet `time` som returnerer uptimenanos som int, og er en expression (leaf node selvsagt).
- Nøkkelordet `print` som (optionally) kan etterføles av `ln` for `println`-behaviour. Deretter skal print-typen spesifiseres, dvs. for eksempel `int` dersom det er en `int`-expression som skal printes, eller `string` dersom det skal printes en `string` (som tidligere må være allokert på heap. For `string`-printing skal det passes inn en expression som vil regnes som en peker til heap).
- 