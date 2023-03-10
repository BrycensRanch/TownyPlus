import mineflayer from 'mineflayer'
import { plugin } from 'mineflayer-pvp';
import { mineflayer as mineflayerViewer } from 'prismarine-viewer'
import {execa, execaCommand} from 'execa'
import {Rcon} from 'rcon-client'

import Query from 'minecraft-query';

import afs from 'node:fs/promises';
import fs from 'node:fs';

export const sleep = ms => new Promise(r => setTimeout(r, ms));
async function runConsoleCommand(command, rcon) {
  // const { stdout, stderr } = await execaCommand(`screen -S mc -p 0 -X stuff '${command}^M'`);
  // if (stderr) {
  //   throw new Error(stderr);
  // }
  // return stdout;
  return await rcon.send(command)
}


async function bot(command) {

process.stdin.resume();
process.stdin.setEncoding('utf-8');

let inputString: any = '';
let currentLine = 0;

process.stdin.on('data', inputStdin => {
    inputString += inputStdin;
});

process.stdin.on('end', _ => {
    inputString = inputString.replace(/\s*$/, '')
        .split('\n')
        .map(str => str.replace(/\s*$/, ''));

    main();
});

function readLine() {
    return inputString[currentLine++];
}

function main() {
    const ws = fs.createWriteStream(process.env.OUTPUT_PATH as string);

    const n = parseInt(readLine(), 10); // Read and integer like this

    // Read an array like this
    const c = readLine().split(' ').map(cTemp => parseInt(cTemp, 10));

    let result; // result of some calculation as an example

    ws.write(result + "\n");

    ws.end();
}
  // const rcon = await Rcon.connect({
  //   host: 'localhost',
  //   port: 25575,
  //   password: 'theroadtohellispavedwithgoodintentions'
  
  
  // })
  // const q = new Query({
  //   host: 'localhost',
  //   port: 25565
  // })
  // console.log(await q.fullStat()) 
  const bot = mineflayer.createBot({
  host: 'localhost', // minecraft server ip
//   username: 'email@example.com', // minecraft username
  username: 'OldestAnarchy', // minecraft username
  auth: 'microsoft' // only set if you need microsoft auth, then set to 'microsoft'
  // port: 25565,                // only set if you need a port that isn't 25565
  // version: false,             // only set if you need a specific version or snapshot (ie: "1.8.9" or "1.16.5"), otherwise it's set automatically
  // password: '12345678'        // set if you want to use password-based auth (may be unreliable)
})

bot.loadPlugin(plugin)

bot.once('spawn', async() => {
    await mineflayerViewer(bot, { port: 3007, firstPerson: false }) // port is the minecraft server port, if first person is false, you get a bird's-eye view
    await bot.waitForChunksToLoad()
    await bot.waitForTicks(20)
    // Make sure the bot is operator
    // await runConsoleCommand(`op ${bot.username}`, rcon);
    console.log(`The bot has logged in as: ${bot.username}. Ping is ${bot.player.ping}ms.` ) 
    console.log(`The bot is in the world: ${bot.world.name} and is at coordinates: ${bot.entity.position} `)
    console.log(`Joined using Minecraft version ${bot.version}`)
    // bot.chat(command)
    // console.log(`The bot has ran the command: ${command}`)
    
    const msg = await bot.awaitMessage()
    console.log(`Got message: ${msg.toString()}`)
    console.log(`Now exiting...`)

    process.exit(0);
})

bot.on('chat', (username, message) => {
  if (username === bot.username) return;
    console.log(username, message)
    
  // bot.chat(message)
})
// bot.on('entityHurt', (entity) => {
//   console.log(entity)
//   if (entity.name === bot.entity.name) {
//     console.log(`The entity ${entity.name} has been hurt, attacking nearest entity ${bot.nearestEntity.name}`)
//   }
//   if (bot.nearestEntity()) {
//     // @ts-expect-error
//     bot.pvp.attack(bot.nearestEntity())
//   }
// })
// @ts-expect-error
bot.on('messagestr', (message, position, jsonMsg, sender, verified) => {
  console.log(message, position, jsonMsg, sender, verified)
})

// Log errors and kick reasons:
bot.on('kicked', (reason, loggedIn) => {
  throw new Error(`Kicked from server ${loggedIn ? 'after logging in' : 'during the login phase'}: ${reason}`)
})
bot.on('error', (error) => {
  throw new Error(`Error in bot: ${error}`)
})

}
bot("/townyplus version").catch((error) => {
  console.log(error)
  process.exit(1)
  })