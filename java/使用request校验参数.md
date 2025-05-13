```java
@PostMapping("/version")
    @ResponseBody
    public Result version(@Valid @RequestBody VersionRequest body) {
        return Result.ok(versionService.version(body));
    }

@GetMapping("/version")
@ResponseBody
public Result version(@Valid @ModelAttribute VersionRequest body) {
    return Result.ok(versionService.version(body));
}
```