package com.ccy.easyzhihu.controller;

import com.ccy.easyzhihu.Service.MessageService;
import com.ccy.easyzhihu.Service.UserService;
import com.ccy.easyzhihu.model.HostHolder;
import com.ccy.easyzhihu.model.Message;
import com.ccy.easyzhihu.model.User;
import com.ccy.easyzhihu.model.VeiwObject;
import com.ccy.easyzhihu.util.ZhiHuUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author chengcongyue
 * @version 1.0
 * @description com.ccy.easyzhihu.controller
 * @date 2019/4/14
 */
@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = {"/msg/addMessage"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("toName")String toName,@RequestParam("content")String content)
    {
        try {
            if(hostHolder.getUser()==null)
            {
                return ZhiHuUtil.getJSONString(999,"未登录");
            }
            User user=userService.selectByName(toName);
            if(user==null)
            {
                return ZhiHuUtil.getJSONString(1,"用户不存在");
            }
            Message msg=new Message();
            msg.setContent(content);
            msg.setFromId(hostHolder.getUser().getId());
            msg.setToId(user.getId());
            msg.setCreatedDate(new Date());
            messageService.addMessage(msg);
            return ZhiHuUtil.getObjectJson(0);
        }catch (Exception e)
        {
            logger.error("增加站内信失败"+e.getMessage());
            return ZhiHuUtil.getJSONString(1,"插入站内信失败");
        }
    }

    @RequestMapping(path = {"/msg/list"},method = {RequestMethod.GET})
    public String conversationDetail(Model model)
    {
        if(hostHolder.getUser()==null)
        {
            return "redirect:/relogin";
        }
        int localUserId=hostHolder.getUser().getId();
        List<Message> conversationList=messageService.getConversationList(localUserId,0,10);
        List<VeiwObject> conversations=new ArrayList<VeiwObject>();
        for(Message message:conversationList)
        {
            VeiwObject vo =new VeiwObject();
            vo.set("message",message);
            int targetId=message.getFromId()==localUserId?message.getToId():message.getFromId();
            vo.set("user",userService.getUser(targetId));
            vo.set("unread",messageService.getConversationUnreadCount(localUserId,message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations",conversations);
        return "letter";
    }

    @RequestMapping(path = {"/msg/detail"},method = {RequestMethod.GET})
    public String conversationDetail(Model model,@Param("conversationId") String conversationId)
    {
        try {
            List<Message> conversationList=messageService.getConversationDetail(conversationId,0,20);
            List<VeiwObject> messages=new ArrayList<>();
            for(Message msg:conversationList)
            {
                VeiwObject vo=new VeiwObject();
                vo.set("message",msg);
                User user=userService.getUser(msg.getFromId());
              /*  if(user==null)
                {
                    continue;
                }*/
                vo.set("user",user);
                messages.add(vo);
            }
            model.addAttribute("messages",messages);
        }catch (Exception e)
        {
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }
}
