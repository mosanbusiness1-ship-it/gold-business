import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

const auctionId = 45;
const baseUrl = 'http://localhost:8081';
const socketUrl = `${baseUrl}/ws-auctions`;

const auctionIdEl = document.getElementById('auctionId') as HTMLElement;
const currentPriceEl = document.getElementById('currentPrice') as HTMLElement;
const winnerEl = document.getElementById('winner') as HTMLElement;
const bidCountEl = document.getElementById('bidCount') as HTMLElement;
const statusEl = document.getElementById('status') as HTMLElement;
const messageEl = document.getElementById('message') as HTMLElement;
const amountInput = document.getElementById('amount') as HTMLInputElement;
const bidButton = document.getElementById('placeBidBtn') as HTMLButtonElement;

interface AuctionSummaryDTO {
  auction_id: number;
  current_highest_bid: number;
  current_highest_bidder_id: number | null;
  current_winner_id: number | null;
  bid_count: number;
  status: string;
  ends_at: string | null;
  updated_at: string | null;
}

function formatPrice(value: number | null | undefined): string {
  return `${value ?? 0} XAF`;
}

function renderSummary(summary: AuctionSummaryDTO) {
  auctionIdEl.textContent = String(summary.auction_id);
  currentPriceEl.textContent = formatPrice(summary.current_highest_bid);
  winnerEl.textContent = summary.current_highest_bidder_id ? String(summary.current_highest_bidder_id) : '-';
  bidCountEl.textContent = String(summary.bid_count ?? 0);
  statusEl.textContent = summary.status === 'ACTIVE' ? 'Live' : 'Closed';
}

function connectAuctionSocket() {
  const client = new Client({
    webSocketFactory: () => new SockJS(socketUrl),
    reconnectDelay: 5000,
    debug: (str) => {
      console.log('[STOMP]', str);
    },
    onConnect: () => {
      statusEl.textContent = 'Connected';
      statusEl.style.color = '#34d399';

      client.subscribe(`/topic/auction.${auctionId}`, (frame) => {
        const payload = JSON.parse(frame.body) as AuctionSummaryDTO;
        renderSummary(payload);
      });

      fetchSummary();
    },
    onDisconnect: () => {
      statusEl.textContent = 'Disconnected';
      statusEl.style.color = '#f87171';
    },
    onStompError: () => {
      statusEl.textContent = 'Socket error';
      statusEl.style.color = '#f87171';
    }
  });

  client.activate();
  return client;
}

async function fetchSummary() {
  try {
    const response = await fetch(`${baseUrl}/public/auctions/${auctionId}/summary`, {
      headers: {
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error('Impossible de charger le summary');
    }

    const summary = (await response.json()) as AuctionSummaryDTO;
    renderSummary(summary);
  } catch (error) {
    console.error(error);
    messageEl.textContent = 'Erreur lors du chargement du résumé de l’enchère.';
    messageEl.className = 'error';
  }
}

async function placeBid() {
  const amount = Number(amountInput.value);

  if (!amount || amount <= 0) {
    messageEl.textContent = 'Le montant doit être positif.';
    messageEl.className = 'error';
    return;
  }

  try {
    messageEl.textContent = 'Envoi de l’offre...';
    messageEl.className = 'status';

    const response = await fetch(`${baseUrl}/public/auctions/${auctionId}/bids`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Authorization: 'Bearer <TOKEN>'
      },
      body: JSON.stringify({
        product_id: 1,
        bidder_id: 1,
        amount
      })
    });

    if (!response.ok) {
      const errorText = await response.text();
      throw new Error(errorText || 'Erreur lors de l’envoi de l’offre');
    }

    const result = await response.json();
    messageEl.textContent = `Offre envoyée : ${result.amount} XAF`;
    amountInput.value = '';
  } catch (error) {
    console.error(error);
    messageEl.textContent = error instanceof Error ? error.message : 'Erreur lors de la mise à jour';
    messageEl.className = 'error';
  }
}

bidButton.addEventListener('click', placeBid);
connectAuctionSocket();
