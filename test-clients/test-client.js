const sleep = require('sleep')
const amqp = require('amqplib')

const health = require('./data/health.js')
const relations = require('./data/relations.js')

console.log('Usage: npm start <orgId>')
sleep.sleep(20)

const config = {
    host: 'localhost',
    user: 'guest',
    password: 'guest',
    vhost: '',
    orgId: process.argv[2] === undefined ? 'mock.no' : process.argv[2]
}

console.log(`Using orgId: ${config.orgId}`)

const connectionString = `amqp://${config.user}:${config.password}@${config.host}:5672/${config.vhost}`
const queue = `${config.orgId}.downstream`

const consumeMsg = (channel) => {
    return channel.assertQueue(queue, { durable: true }).then((ok) => {
            return channel.consume(queue, (msg) => {
                const event = JSON.parse(msg.content.toString())
                console.log(`${event.action} received, corrId: ${event.corrId}`)

            if (event.action === 'HEALTH') {
        health.corrId = event.corrId
        health.orgId = event.orgId
        channel.publish('', msg.properties.replyTo, new Buffer(JSON.stringify(health)), { 'contentType': 'application/json' })
    } else {
        let replyMessage = {}
        if (event.action === 'GET_RELATIONS') {
            replyMessage = relations
        }

        replyMessage.corrId = event.corrId
        replyMessage.orgId = event.orgId
        channel.publish('', `${config.orgId}.upstream`, new Buffer(JSON.stringify(replyMessage)), { 'contentType': 'application/json' })
    }

    channel.ack(msg)
})
})
}

const connection = amqp.connect(connectionString)
connection.then((con) => {
    return con.createChannel()
}).then((channel) => {
    return consumeMsg(channel)
}).catch(console.warn)
