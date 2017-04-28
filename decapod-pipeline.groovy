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

            stage('Install decapod') {
                salt.enforceState(saltMaster, 'I@salt:master', ['decapod.libs'], true)
                salt.enforceState(saltMaster, 'I@salt:master', ['decapod.server'], true)
            }

            stage('Configure nodes') {
                salt.runSaltProcessStep(saltMaster, 'ceph*', 'saltutil.sync_all', [], null, true)
                salt.runSaltProcessStep(saltMaster, 'ceph*', 'mine.send', ['grains.items'], null, true)
                salt.runSaltProcessStep(saltMaster, 'ceph*', 'mine.send', ['network.get_hostname'], null, true)
                salt.enforceState(saltMaster, 'ceph*', ['decapod.discover'], true)
            }

            stage('Deploy ceph cluster') {
                salt.enforceState(saltMaster, 'I@salt:master', ['decapod.configure_cluster'], true)
            }

        } catch (Throwable e) {
            currentBuild.result = 'FAILURE'
            throw e
        } finally {

        }
    }
}
