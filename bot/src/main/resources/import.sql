/* default templates */
INSERT INTO template (id, template_id, instance_type, instance_id, title, content, footer)
VALUES (
	'83dca784-b379-4611-8285-111c2b99ecfa',
   'command.response.codesearch.ok',
   null,
   null,
   'CodeSearch - {{{ result.name }}}',
'{{{ result.description }}}

{{#if result.first_seen_in.length~}}
> added in: {{{ result.first_seen_in }}}
{{/if~}}
```{{ result.language }}

{{{ result.definition }}}
```

{{#if result.data.parameters.length~}}
**Parameters:**
{{#each result.data.parameters~}}
- **{{{name}}}**: {{{description}}}
{{/each~}}
{{/if}}
{{#if notes.length~}}
**Notes:** :warning:
{{#each notes~}}
- {{{ . }}}
{{/each~}}
{{/if}}
**Source Reference**:
[{{{ result.source_file }}}#L{{ result.source_line }}-L{{ result.source_line_end }}]({{{ result.source_link }}})
',
	'Filtered from {{{ result_count }}} results.'
);
INSERT INTO template (id, template_id, instance_type, instance_id, title, content, footer)
VALUES (
	'96c48501-5e10-4615-8d6d-f495efda40fd',
	'command.response.codesearch.err',
	null,
	null,
	'CodeSearch - No results',
	'No results found for `{{{ query }}}`',
	null
);

/* qube: sourcecode */
INSERT INTO sourceindex_project (id, key, display_name, description, repository_kind, repository_remote, repository_commit_hash, repository_tag, type, created_at, updated_at)
VALUES (
   'd69b3556-ff1e-4815-9a78-2ecd58a0b0cd',
   'github.com/twitch4j/twitch4j',
   'Twitch4J',
   'A Java wrapper for the Twitch API',
   'git',
   'https://github.com/twitch4j/twitch4j.git',
   'db1b95168559e47ad211fbb70e4d7e4be4d5fb27',
   '1.13.0',
   'JAVA',
   '2023-03-30T00:00:00Z',
   '2023-03-30T00:00:00Z'
);
