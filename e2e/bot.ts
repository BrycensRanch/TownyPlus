import mineflayer, {Bot} from 'mineflayer'
import { plugin } from 'mineflayer-pvp';
import { mineflayer as mineflayerViewer } from 'prismarine-viewer'
import {execa, execaCommand} from 'execa'
import {Rcon} from 'rcon-client'

import Query from 'minecraft-query';

import afs from 'node:fs/promises';
import fs from 'node:fs';
import path from 'path';
import { fileURLToPath } from 'url';
type Nullable<T> = T | null;


const __filename = fileURLToPath(import.meta.url);

const __dirname = path.dirname(__filename);

export const sleep = ms => new Promise(r => setTimeout(r, ms));

let currentBot: Nullable<Bot> = null;
async function runConsoleCommand(command, rcon) {
  // const { stdout, stderr } = await execaCommand(`screen -S mc -p 0 -X stuff '${command}^M'`);
  // if (stderr) {
  //   throw new Error(stderr);
  // }
  // return stdout;
  return await rcon.send(command)
}


async function bot(commands: string[] = []) {

process.stdin.resume();
process.stdin.setEncoding('utf-8');

let inputString: any = '';
let currentLine = 0;

process.stdin.on('data', inputStdin => {
    console.log("data", inputStdin)
    inputString += inputStdin;
    main();
    // @ts-ignore
    currentBot?.chat(inputStdin.replace(/\s*$/, '')        .split('\n')
        .map(str => str.replace(/\s*$/, '')).join("")
    )

});

process.stdin.on('end', _ => {
    console.log("got end of input")
    inputString = inputString.replace(/\s*$/, '')
        .split('\n')
        .map(str => str.replace(/\s*$/, ''));

    main();
});

function readLine() {
    return inputString[currentLine++];
}

function main() {
    // const ws = fs.createWriteStream(process.env.OUTPUT_PATH as string);
    //
    // const n = parseInt(readLine(), 10); // Read and integer like this
    //
    // // Read an array like this
    // const c = readLine().split(' ').map(cTemp => parseInt(cTemp, 10));
    //
    // let result; // result of some calculation as an example
    //
    // ws.write(result + "\n");

    // ws.end();
    console.log(readLine())
    console.log(__dirname, __filename)
    // currentBot?.chat(readLine())
}
  const rcon = await Rcon.connect({
    host: 'localhost',
    port: 25575,
    password: 'theroadtohellispavedwithgoodintentions'
  
  
  })
  const q = new Query({
    host: 'localhost',
    port: 25565
  })
  console.log(await q.fullStat())
    // @ts-expect-error
  const bot = mineflayer.createBot({
  host: 'localhost', // minecraft server ip
//   username: 'email@example.com', // minecraft username
//   username: 'OldestAnarchy', // minecraft username
  auth: 'microsoft', // only set if you need microsoft auth, then set to 'microsoft'
       disableChatSigning: true
  // port: 25565,                // only set if you need a port that isn't 25565
  // version: false,             // only set if you need a specific version or snapshot (ie: "1.8.9" or "1.16.5"), otherwise it's set automatically
  // password: '12345678'        // set if you want to use password-based auth (may be unreliable)
})
    currentBot = bot;

bot.loadPlugin(plugin)

bot.once('spawn', async() => {
    await mineflayerViewer(bot, { port: 3007, firstPerson: false }) // port is the minecraft server port, if first person is false, you get a bird's-eye view
    await bot.waitForChunksToLoad()
    await bot.waitForTicks(20)
    // Make sure the bot is operator
    await runConsoleCommand(`op ${bot.username}`, rcon);
    console.log(`The bot has logged in as: ${bot.username}. Ping is ${bot.player.ping}ms.` ) 
    console.log(`The bot is in the world: ${bot.world.name} and is at coordinates: ${bot.entity.position} `)
    console.log(`Joined using Minecraft version ${bot.version}`)
    const loadedCommands = (await bot.tabComplete("/")).map((loadedCmd) => loadedCmd.match);
    console.log(`The bot has loaded the following commands: ${loadedCommands}`)
    // @ts-ignore
    if (!loadedCommands.includes("townyplus")) {
        throw new Error("The bot does not have access to the townyplus plugin or the plugin is not loaded.")
    }
    else {
        console.log("The bot has access to the townyplus plugin")
    }
    for (const command of commands) {
        // await bot.tabComplete(command, true)
        await bot.chat(command)
        await bot.awaitMessage(/(.*townyplus|reload|version|opened|done|showing|toggled|successfully|uploaded|town.*)/gi)
    }
    // Does the bot need to exit out mof the dist folder?
    let baseDir = path.join(__dirname, '..')
    if (!fs.existsSync(path.join(baseDir, 'run'))) {
        baseDir = path.join(baseDir, '..')
    }
    const logLines: string[] = [];
    const logFile = path.join(baseDir, 'run', 'logs', 'latest.log')
    const logFileStream = fs.createReadStream(logFile)
    console.log(`Reading log file at ${logFile} for exceptions or errors. If the server closes unexpectedly, the bot will exit with an error code. If the server has an exception, the bot will exit with an error code.`)
    logFileStream.on('data', (data) => {
        if (data.toString().includes("Closing Server")) {
            console.error("Server closed unexpectedly.");
            console.log(data.toString())
            console.error("Server closed unexpectedly.");

            process.exit(1);
        }
        // else if () {
        //     console.error("Server had an exception. Exiting.");
        //     console.log(data.toString())
        //     console.error("Server had an exception. Exiting.");
        //     process.exit(1);
        // }
        else {
            logLines.concat(data.toString().split(/\r?\n/))
        }
    })
    logFileStream.on('end', () => {
        console.log("end")
    })
    logFileStream.on('error', (err) => {
        console.log(err)
        throw err;
    })
    for (const line of logLines) {
        if (line.includes("Closing Server")) {
            console.error("Server closed unexpectedly.");
            console.log(line)
            console.error("Server closed unexpectedly.");

            process.exit(1);
        }
        if (line.toString().toLowerCase().includes("exception") || line.toString().toLowerCase().includes("error")) {
            console.error("Server had an exception. Exiting.");
            console.log(line)
            console.error("Server had an exception. Exiting.");
            process.exit(1);
        }
    }

    // console.log(`The bot has ran the command: ${command}`)
    // Keep the bot in the server
    // const msg = await bot.awaitMessage()
    // console.log(`Got message: ${msg.toString()}`)
    // console.log(`Now exiting...`)

    await sleep(3000)
    console.log("Woo! All tests passed!")
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
// bot.on('messagestr', (message, position, jsonMsg, sender, verified) => {
//   console.log(message, position, jsonMsg, sender, verified)
// })

// Log errors and kick reasons:
bot.on('kicked', (reason, loggedIn) => {
  throw new Error(`Kicked from server ${loggedIn ? 'after logging in' : 'during the login phase'}: ${reason}`)
})
bot.on('error', (error) => {
  throw new Error(`Error in bot: ${error}`)
})

}
bot(["/townyplus version", "/townyplus", "/townyplus reload", "/tasktest", "/tchest", "/townyplus bypass on", "/townyplus dump"]).catch((error) => {
  console.log(error)
  process.exit(1)
  })