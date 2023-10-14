# DoDoSRV

一个 Spigot 插件，允许您将 Minecraft 游戏服务器内的活动转发到 Dodo（反之亦然）。

**警告：此插件正处于早期开发阶段，功能暂不完整，且未针对大型繁忙群组优化。配置和语言文件可能无法跨版本继承通用，在不同版本见需要您手动合并更改。**

## 命令

* /dodosrv link - 权限: dodosrv.link 说明：连接当前 Minecraft 角色到您的 Dodo 账户
* /dodosrv unlink - 权限：dodosrv.unlink 说明：与已绑定的 Dodo 账户断开连接
* /dodosrv editintegral <已绑定dodo的MC角色用户名> <操作代码 1=增加积分 2=扣除积分> <操作的积分数量> - 权限：dodosrv.editintegral 说明：修改指定 MC 角色的绑定的 Dodo 账户的积分

## 演示

![Snipaste_2023-10-14_23-32-40](https://github.com/Ghost-chu/DoDoSRV/assets/30802565/ba3215bd-f006-4b0b-b07f-52353da71c73)
![Snipaste_2023-10-14_23-29-10](https://github.com/Ghost-chu/DoDoSRV/assets/30802565/6701757b-8c95-43c9-a89e-025fe49890fe)
![Snipaste_2023-10-14_23-24-33](https://github.com/Ghost-chu/DoDoSRV/assets/30802565/e8780a59-162d-45a2-b0d0-27045df231f9)
<img width="830" alt="example" src="https://github.com/Ghost-chu/DoDoSRV/assets/30802565/f564d3f1-2468-4907-8171-98dcc78fbd42">
![Snipaste_2023-10-14_23-20-43](https://github.com/Ghost-chu/DoDoSRV/assets/30802565/b0643cbb-7995-4d2e-993a-24a69225f04e)
![Snipaste_2023-10-14_23-19-54](https://github.com/Ghost-chu/DoDoSRV/assets/30802565/38503d8f-a066-4724-859d-ff98c9ce545b)
<img width="830" alt="A4548DE5E8A2A4D3F5488AC4A24D5DBC" src="https://github.com/Ghost-chu/DoDoSRV/assets/30802565/a5c5d040-0113-490a-97d9-3b3a294e819c">

## 配置文件

[config.yml](https://github.com/Ghost-chu/DoDoSRV/blob/master/src/main/resources/config.yml)

## 使用的库

[Ghost-chu/Dodo.java](https://github.com/Ghost-chu/Dodo.java) 基于 [DeeChael/Dodo.java](https://github.com/DeeChael/Dodo.java) 改进
