package com.gameApi.controller;

import com.gameApi.common.Result;
import com.gameApi.entity.OutNoticeEntity;
import com.gameApi.request.NoticeRequest;
import com.gameApi.request.ServerListRequest;
import com.gameApi.request.VersionRequest;
import com.gameApi.service.IndexService;
import com.gameApi.service.NoticeService;
import com.gameApi.service.ServerListService;
import com.gameApi.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Controller
public class IndexController {
    @Autowired
    private IndexService indexService;

    @PostMapping("/report")
    @ResponseBody
    public Result report(@Valid @RequestBody Map<String, Object> body) {
        return Result.ok(indexService.report(body));
    }

    @PostMapping(path = "/report_v2", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String reportV2(@Valid @RequestParam Map<String, Object> body) {
        return Result.stringResultWithBom(Result.ok(indexService.report(body)));
    }

    @Autowired
    private VersionService versionService;
    @PostMapping(path = "/version", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String version(@Valid VersionRequest body) {
        return Result.stringResultWithBom(versionService.version(body));
    }
    @GetMapping("reload_cache")
    @ResponseBody
    public void reloadCache () {
        versionService.reloadCache();
    }

    @Autowired
    private ServerListService serverListService;
     @PostMapping(path = "/server_list", produces = "text/plain;charset=UTF-8")
    @ResponseBody
     public String serverList(@Valid ServerListRequest body) {
          return Result.stringResultWithBom(serverListService.serverList(body));
     }

    @Autowired
    private NoticeService noticeService;
    @PostMapping(path = "/notice", produces = "text/plain;charset=UTF-8")
    @ResponseBody
    public String notice (@Valid NoticeRequest body) {
        return Result.stringResultWithBom(noticeService.get(body));
    }
    @GetMapping("reload_notice")
    @ResponseBody
    public void reloadNotice () {
        noticeService.reloadRulesFromDB();
    }
}
