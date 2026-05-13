// Code scaffolded by goctl. Safe to edit.
// goctl 1.10.1

package svc

import (
	"civet/apps/admin/api/internal/config"
	"civet/apps/admin/model"

	// 这一行很重要
	_ "github.com/lib/pq"
	"github.com/zeromicro/go-zero/core/stores/sqlx"
)

type ServiceContext struct {
	Config config.Config
	// Code generated model fields. DO NOT EDIT.
	SysConfigModel model.SysConfigModel
	// End generated model fields.
}

func NewServiceContext(c config.Config) *ServiceContext {
	conn := sqlx.NewSqlConn(c.Pg.DriverName, c.Pg.DataSource)

	return &ServiceContext{
		Config: c,
		// Code generated model init. DO NOT EDIT.
		SysConfigModel: model.NewSysConfigModel(conn),
		// End generated model init.
	}
}
