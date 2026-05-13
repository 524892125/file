pipeline {
    agent any

    parameters {
        string(name: 'MSG_ID', defaultValue: '', description: 'Feishu card message_id')
    }

    environment {
        PATH = "${env.PATH};D:\\nodejs;C:\\Users\\Administrator\\AppData\\Roaming\\npm"
        CALLBACK_UPDATE_URL = "http://114.55.150.157:9107/jenkins/callback/update"
        JOB_INFO = "${JOB_NAME} #${BUILD_NUMBER}"
    }

    stages {
        stage('Build Process') {
            steps {
                script {

                    // 👉 开始构建
                    if (params.MSG_ID?.trim()) {
                        powershell """
                            \$body = @{
                                msg_id = "${params.MSG_ID}"
                                build_name = "${env.JOB_NAME}"
                                build_number = "${env.BUILD_NUMBER}"
                                build_url = "${env.BUILD_URL}"
                                status = "开始构建"
                            } | ConvertTo-Json

                            Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                        """
                    }

                    try {
                        dir('D:/guanwang_code/kiif_ase_website') {
                            bat "git reset --hard && git clean -fd && git pull"
                        }

                        if (params.MSG_ID?.trim()) {
                            powershell """
                                \$body = @{
                                    msg_id = "${params.MSG_ID}"
                                    status = "拉代码完成 (30%)"
                                } | ConvertTo-Json
                                Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                            """
                        }

                        dir('D:/guanwang_code/kiif_ase_website') {
                            // bat 'pnpm install --frozen-lockfile'
                        }

                        if (params.MSG_ID?.trim()) {
                            powershell """
                                \$body = @{
                                    msg_id = "${params.MSG_ID}"
                                    status = "构建完成 (60%)"
                                } | ConvertTo-Json
                                Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                            """
                        }

                        dir('D:/guanwang_code/kiif_ase_website') {
                            // bat 'pnpm build'
                        }

                        if (params.MSG_ID?.trim()) {
                            powershell """
                                \$body = @{
                                    msg_id = "${params.MSG_ID}"
                                    status = "构建已完成"
                                } | ConvertTo-Json
                                Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                            """
                        }

                    } catch (Exception e) {
                        env.BUILD_ERROR_DETAIL = e.getMessage()
                        error "构建过程中出错: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: 'guanwang_server', usernameVariable: 'U', passwordVariable: 'P')]) {
                        powershell """
                            \$winscp = 'D:\\WinSCP\\WinSCP.com'
                            \$localPath = 'D:/guanwang_code/kiif_ase_website/.output/server'
                            \$remotePath = '/www/wwwroot/website'
                            \$archiveName = 'dist.tar.gz'
        
                            # --- 1. 本地打包 (Windows 10+ 自带 tar) ---
                            Write-Host "正在本地打包..."
                            cd \$localPath
                            tar -czvf \$archiveName *
        
                            # --- 2. 编写 WinSCP 脚本上传 ---
                            \$script = @(
                                "option batch on"
                                "option confirm off"
                                "open sftp://${U}:${P}@8.140.244.117/ -hostkey=*" 
                                "lcd \$localPath" 
                                "cd \$remotePath"
                                "put \$archiveName" # 只上传这一个压缩包
                                
                                # --- 3. 核心：通过 WinSCP call 执行远程解压 ---
                                "call tar -xzvf \$archiveName -C \$remotePath"
                                "call rm \$archiveName" # 解压完删除远程压缩包
                                
                                "exit"
                            )
        
                            \$script | Out-File -Encoding ASCII winscp.txt
                            & \$winscp /script=winscp.txt
                            
                            # --- 4. 清理本地压缩包 ---
                            Remove-Item winscp.txt
                            Remove-Item "\$localPath/\$archiveName"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                if (params.MSG_ID?.trim()) {
                    powershell """
                        \$body = @{
                            msg_id = "${params.MSG_ID}"
                            build_name = "${env.JOB_NAME}"
                            build_number = "${env.BUILD_NUMBER}"
                            build_url = "${env.BUILD_URL}"
                            status = "success"
                        } | ConvertTo-Json

                        Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                    """
                }
            }
        }

        aborted {
            script {
                if (params.MSG_ID?.trim()) {
                    powershell """
                        \$log = ""
                        try {
                            \$log = (Get-Content "${env.JENKINS_HOME}\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log" -Tail 10 -ErrorAction SilentlyContinue) -join "\\n"
                        } catch {}

                        \$body = @{
                            msg_id = "${params.MSG_ID}"
                            build_name = "${env.JOB_NAME}"
                            build_number = "${env.BUILD_NUMBER}"
                            build_url = "${env.BUILD_URL}"
                            status = "构建失败，构建被中断"
                            log_tail = \$log
                        } | ConvertTo-Json

                        Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                    """
                }
            }
        }

        failure {
            script {
                if (params.MSG_ID?.trim()) {
                    powershell """
                        \$log = ""
                        try {
                            \$log = (Get-Content "${env.JENKINS_HOME}\\jobs\\${env.JOB_NAME}\\builds\\${env.BUILD_NUMBER}\\log" -Tail 10 -ErrorAction SilentlyContinue) -join "\\n"
                        } catch {}

                        \$body = @{
                            msg_id = "${params.MSG_ID}"
                            build_name = "${env.JOB_NAME}"
                            build_number = "${env.BUILD_NUMBER}"
                            build_url = "${env.BUILD_URL}"
                            status = "构建失败"
                            log_tail = \$log
                        } | ConvertTo-Json

                        Invoke-RestMethod -Uri "${env.CALLBACK_UPDATE_URL}" -Method Patch -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes(\$body))
                    """
                }
            }
        }
    }
}