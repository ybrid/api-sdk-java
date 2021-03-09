/*
 * Copyright (c) 2019 nacamar GmbH - Ybrid®, a Hybrid Dynamic Live Audio Technology
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ybrid.api;

import io.ybrid.api.driver.FactorySelector;
import io.ybrid.api.driver.Driver;
import io.ybrid.api.metadata.MetadataMixer;
import io.ybrid.api.metadata.source.Source;
import io.ybrid.api.metadata.source.SourceType;
import io.ybrid.api.session.Command;
import io.ybrid.api.session.PlayerControl;
import io.ybrid.api.session.Request;
import io.ybrid.api.transaction.SessionTransaction;
import io.ybrid.api.transaction.Transaction;
import io.ybrid.api.transport.ServiceTransportDescription;
import io.ybrid.api.transport.ServiceURITransportDescription;
import io.ybrid.api.util.Connectable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This class implements an actual session with a Ybrid server.
 *
 * The session can be used to request an audio stream from the server.
 * It is also used to control the stream.
 */
public final class Session implements Connectable, KnowsSubInfoState {
    static final Logger LOGGER = Logger.getLogger(Session.class.getName());

    private final @NotNull Source source = new Source(SourceType.SESSION);
    private final @NotNull WorkaroundMap activeWorkarounds = new WorkaroundMap();
    private final @NotNull MetadataMixer metadataMixer;
    private final @NotNull Server server;
    private final @NotNull MediaEndpoint mediaEndpoint;
    private @Nullable PlayerControl playerControl = null;
    private @Nullable Driver driver;

    private @NotNull Driver getDriver() {
        if (driver != null)
            return driver;

        LOGGER.info("Connecting driver...");
        try {
            this.driver = FactorySelector.getFactory(server, mediaEndpoint).getDriver(this);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Selected driver: " + driver.getClass().getName());

        return driver;
    }

    Session(@NotNull Server server, @NotNull MediaEndpoint mediaEndpoint) {
        this.server = server;
        this.mediaEndpoint = mediaEndpoint;
        this.metadataMixer = new MetadataMixer(this);

        activeWorkarounds.merge(mediaEndpoint.getWorkarounds());
        activeWorkarounds.merge(server.getWorkarounds());
    }

    /**
     * Gets the {@link MetadataMixer} for this session.
     * @return Gets the current {@link MetadataMixer}.
     */
    public @NotNull MetadataMixer getMetadataMixer() {
        return metadataMixer;
    }

    /**
     * Gets the {@link MediaEndpoint} used for this session.
     * @return Returns the {@link MediaEndpoint}.
     */
    public @NotNull MediaEndpoint getMediaEndpoint() {
        return mediaEndpoint;
    }

    /**
     * Gets the {@link Server} object used to communicate with the server.
     * @return Returns the {@link Server} object.
     * @deprecated The {@link Server} was deprecated.
     */
    @Deprecated
    @ApiStatus.ScheduledForRemoval
    public @NotNull Server getServer() {
        return server;
    }

    public @NotNull CapabilitySet getCapabilities() {
        return getDriver().getCapabilities();
    }

    /**
     * Creates a transaction for this session.
     * @param request The request for the transaction.
     * @return The newly created transaction.
     */
    @Contract("_ -> new")
    public @NotNull SessionTransaction createTransaction(@NotNull Request request) {
        return new SessionTransaction(this, request, this::executeTransaction);
    }

    private void executeTransaction(@NotNull Transaction transaction) throws IOException {
        final @NotNull Request request = ((SessionTransaction)transaction).getRequest();

        // Ensure we run all transactions with a valid driver.
        getDriver();

        try {
            switch (request.getCommand()) {
                case CONNECT_INITIAL_TRANSPORT:
                case RECONNECT_TRANSPORT: {
                    final @Nullable Map<String, Double> acceptedMediaFormats = playerControl != null ? playerControl.getAcceptedMediaFormats() : null;
                    final @NotNull ServiceTransportDescription transportDescription = new ServiceURITransportDescription(new Source(SourceType.TRANSPORT), getDriver().getCurrentService(), metadataMixer, acceptedMediaFormats, mediaEndpoint.getAcceptedLanguages(), transaction, getActiveWorkarounds(), getDriver().getStreamURI(), null);

                    Objects.requireNonNull(playerControl).connectTransport(transportDescription);
                    break;
                }
                default:
                    getDriver().executeRequest(request);
            }
        } catch (Exception e) {
            throw new IOException(e);
        }

        switch (request.getCommand()) {
            case CONNECT:
            case REFRESH:
                metadataMixer.accept(getDriver().getBouquet());
                break;
        }
    }

    public @NotNull PlayoutInfo getPlayoutInfo() {
        getDriver().clearChanged(SubInfo.PLAYOUT);
        return getDriver().getPlayoutInfo();
    }

    @Override
    public boolean hasChanged(@NotNull SubInfo what) {
        if (driver == null) {
            return metadataMixer.hasChanged(what);
        } else {
            return metadataMixer.hasChanged(what) || driver.hasChanged(what);
        }
    }

    /**
     * Attaches a new player by setting the player's {@link PlayerControl} interface.
     *
     * @param playerControl The player's {@link PlayerControl} interface.
     */
    public void attachPlayer(@NotNull PlayerControl playerControl) {
        if (this.playerControl == playerControl)
            return;

        if (this.playerControl != null) {
            detachPlayer(this.playerControl);
        }

        this.playerControl = playerControl;

        LOGGER.info("Attaching new player");
        this.playerControl.onAttach(this);
    }

    /**
     * Detaches a player by using the player's {@link PlayerControl} interface.
     *
     * @param playerControl The player's {@link PlayerControl} interface.
     */
    public void detachPlayer(@NotNull PlayerControl playerControl) {
        if (this.playerControl == playerControl) {
            LOGGER.info("Detaching current player");
            this.playerControl = null;
            playerControl.onDetach(this);
        } else {
            LOGGER.info("Detach of not-attached player ignored");
        }
    }

    /**
     * Gets the map of currently active workarounds.
     * This map can be updated by the caller if needed.
     * However special care must be taken when doing so to avoid data corruption.
     * Generally applications that wish to communicate workaround settings
     * should use {@link MediaEndpoint#getWorkarounds()} or {@link Server#getWorkarounds()}.
     * Those methods are always safe to use.
     *
     * @return The map of active workarounds.
     * @see MediaEndpoint#getWorkarounds()
     * @see Server#getWorkarounds()
     */
    public @NotNull WorkaroundMap getActiveWorkarounds() {
        return activeWorkarounds;
    }

    /**
     * Gets the {@link Source} for this session.
     *
     * @return The {@link Source} for this session.
     */
    @Contract(pure = true)
    public @NotNull Source getSource() {
        return source;
    }

    @Override
    public boolean isConnected() {
        if (driver == null)
            return false;
        return driver.isConnected();
    }

    @Override
    public void connect() throws IOException {
        final @NotNull Transaction transaction = createTransaction(Command.CONNECT.makeRequest());
        final @Nullable Throwable error;

        transaction.run();
        error = transaction.getError();

        if (error == null)
            return;

        if (error instanceof IOException)
            throw (IOException)error;

        throw new IOException(error);
    }

    @Override
    public void disconnect() {
        try {
            createTransaction(Command.DISCONNECT.makeRequest()).run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        if (driver != null)
            driver.clearChanged(SubInfo.VALIDITY);
        return driver.isValid();
    }

    @Override
    public void close() throws IOException {
        getDriver().close();
    }
}
