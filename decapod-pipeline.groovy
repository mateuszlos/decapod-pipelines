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
              salt.enforceState(saltMaster, 'I@salt:master', ['decapod.server'])
            }

            stage('Deploy ceph cluster') {
              salt.enforceState(saltMaster, 'I@salt:master', ['decapod.configure_cluster'])
            }


        } catch (Throwable e) {
            currentBuild.result = 'FAILURE'
            throw e
        } finally {

        }
    }
}
