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
                            bat 'pnpm build'
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
                    withCredentials([usernamePassword(
                        credentialsId: 'guanwang_server',
                        usernameVariable: 'U', // 缩短变量名避免解析干扰
                        passwordVariable: 'P'
                    )]) {
                        
                        // 注意：这里使用双引号 "" 允许 Groovy 直接注入变量值
                        // 使用 ${U} 和 ${P} 而不是 $env:DEPLOY_USER
                        powershell """
                            \$winscp = 'D:\\WinSCP\\WinSCP.com'
                            
                            \$script = @(
                                "open sftp://${U}:${P}@8.140.244.117/ -hostkey=*" 
                                "lcd D:/guanwang_code/kiif_ase_website/.output"
                                "cd /www/wwwroot/website"
                                "put -r *"
                                "exit"
                            )
                            
                            \$script | Out-File -Encoding ASCII winscp.txt
                            & \$winscp /script=winscp.txt
                            Remove-Item winscp.txt # 部署完删除临时文件防止密码泄露
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