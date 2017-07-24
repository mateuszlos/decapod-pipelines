salt = new com.mirantis.mk.Salt()

timestamps {
    node {
        try {
            def saltMaster
            stage('Connect to Salt API') {
                saltMasterHost = SALT_MASTER_IP
                saltPort = SALT_MASTER_PORT
                SALT_MASTER_URL = "http://${saltMasterHost}:${saltPort}"
                saltMaster = salt.connection(SALT_MASTER_URL, SALT_MASTER_CREDENTIALS)
            }

            stage('Configure telegraf integration') {
                salt.runSaltProcessStep(saltMaster, 'I@salt:master', 'decapod.telegraf')
            }

            stage('Update telegraf configuration') {
                salt.runSaltProcessStep(saltMaster, 'ceph*', 'decapod.update_telegraf_conf')
            }

        } catch (Throwable e) {
            currentBuild.result = 'FAILURE'
            throw e
        } finally {

        }
    }
}
