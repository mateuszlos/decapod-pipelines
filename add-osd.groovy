salt = new com.mirantis.mk.Salt()

timestamps {
    node {
        try {
            def saltMaster
            stage('Connect to Salt API') {
                saltMasterHost = SALT_MASTER_IP
                saltPort = SALT_MASTER_PORT
                SALT_MASTER_URL = "http://${saltMasterHost}:${saltPort}"
                NODES_LIST = ""
                saltMaster = salt.connection(SALT_MASTER_URL, SALT_MASTER_CREDENTIALS)
            }

            stage('delete monitors') {
                 salt.runSaltProcessStep(saltMaster, 'I@salt:master', 'state.sls', 'decapod.add_osd', "  pillar=\'{"decapod_lcm": "add_osd": [${NODES_LIST}]}\'")
            }

        } catch (Throwable e) {
            currentBuild.result = 'FAILURE'
            throw e
        } finally {

        }
    }
}
