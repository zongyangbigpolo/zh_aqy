#!/bin/sh
set -eu

echo "Importing Zh_AqY initialization SQL files..."
mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < /release-sql/ry_20240629.sql
mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < /release-sql/quartz.sql
mysql --protocol=socket -uroot -p"${MYSQL_ROOT_PASSWORD}" "${MYSQL_DATABASE}" < /release-sql/zh_aqy_schema.sql
echo "Zh_AqY initialization SQL import finished."
