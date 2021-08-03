SELECT id, name
FROM vehicle
WHERE 1 = 1
    /*[# th:if="${name} != null"]*/
    /*[# mb:bind="patternName=|${#likes.escapeWildcard(name)}%|" /]*/
  AND name LIKE /*[# mb:p="patternName"]*/ 'Avalon' /*[/]*/
/*[/]*/
ORDER BY id