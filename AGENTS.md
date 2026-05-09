# AGENTS.md instructions for C:\Users\QwQ\Documents\GitHub\Flexgram

在你给我写代码或改代码时，必须遵守：
1. 拒绝过度封装：简单逻辑直接内联，非必要不拆分新函数。
2. 强制优先复用：写新代码前必须先使用项目中现成的接口和工具。
3. 克制防御编程：信任内部数据，仅在必要边界做判空和类型检查。不要使用过多判断。
4. 避免冗杂：功能模块按功能拆分开，防止单代码文件过大。
5. 使用命令行编辑文件前请注意文件内容编码与命令行环境是否匹配；不匹配的情况下不允许编辑，防止产生强制转换导致非英文被破坏。
6. 不要本地运行任何构建/测试，全部使用 CI 测试。
7. 修改完成后提交并推送，使用 CI 验证结果。

Do not introduce new boundary rules / guardrails / blockers / caps (e.g. max-turns), fallback behaviors, or silent degradation just to make it run.
Do not add mock/simulation fake success paths (e.g. returning (mock) ok, templated outputs that bypass real execution, or swallowing errors).
Do not write defensive or fallback code; it does not solve the root problem and only increases debugging cost.
Prefer full exposure: let failures surface clearly (explicit errors, exceptions, logs, failing tests) so bugs are visible and can be fixed at the root cause.
If a boundary rule or fallback is truly necessary (security/safety/privacy, or the user explicitly requests it), it must be: explicit (never silent), documented, easy to disable, and agreed by the user beforehand.
