import { AccountFullInfoDto, TransactionDto } from "../../api/dtos";
import { useParams } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { useAccountFullInfo, useRefreshAccountInfo } from "../../api/bank-account-data-api";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { Card, CardContent, CardMedia, Chip, Divider, IconButton, LinearProgress, Modal, Paper, Stack, Tooltip, Typography } from "@mui/material";
import { formatToUsd } from "../../utils/currencyFormatUtils";
import { useEffect, useState } from "react";
import CategoryList from "../../components/category-list/CategoryList";
import CategoryForm from "../../components/category-form/CategoryForm";
import { useQuery, useQueryClient } from "react-query";
import RefreshIcon from '@mui/icons-material/Refresh';
import ChartList from "../../components/chart-list/ChartList";

const Account = () => {
    const { accountExternalId } = useParams();
    const { currentUser } = useAuth();
    const { data: accountInfo, isLoading } = useAccountFullInfo(currentUser, accountExternalId);
    const { mutate: refreshAccountInfo, isLoading: isRefreshing } = useRefreshAccountInfo();

    const refreshInfo = () => {
        if (accountExternalId) {
            refreshAccountInfo(accountExternalId);
        }
    };

    if (isLoading || isRefreshing) {
        return (
            <div style={{ maxWidth: 1256, margin: 'auto' }}>
                <LinearProgress />
            </div>
        );
    }

    return (
        <div style={{ maxWidth: 1256, margin: 'auto', padding: '32px' }}>
            {accountInfo && (
                <>
                    <Card sx={{ marginBottom: '32px' }} variant="elevation">
                        <CardContent>
                            <div style={{ display: "flex" }}>
                                <CardMedia
                                    sx={{
                                        height: 90,
                                        width: 90,
                                        alignSelf: 'center',
                                        flexDirection: 'column',
                                        marginRight: '24px'
                                    }}
                                    component="img"
                                    image={accountInfo.account.institutionLogo}
                                />

                                <Stack sx={{ flexGrow: 1 }} spacing={0.1}>
                                    <Stack direction="row" spacing={1.5}>
                                        <Typography variant="h2">{accountInfo.account.name}</Typography>
                                        <Chip sx={{ alignSelf: 'center' }} label={accountInfo.account.status} color={accountInfo.account.status === 'ACTIVE' ? 'success' : 'error'} size="small" />
                                        <Tooltip title="Refresh info">
                                            <IconButton size="large" onClick={refreshInfo}>
                                                <RefreshIcon fontSize="inherit" />
                                            </IconButton>
                                        </Tooltip>
                                    </Stack>

                                    <Typography variant="subtitle1">{accountInfo.account.iban}</Typography>
                                    <Typography variant="subtitle1">{accountInfo.account.institutionName}</Typography>
                                    <Typography variant="subtitle1">{`Account currency: ${accountInfo.account.currency}`}</Typography>
                                </Stack>

                                <Typography variant="h4" style={{ alignSelf: 'center' }}>{`Balance: ${formatToUsd(accountInfo.account.balance)}`}</Typography>
                            </div>
                        </CardContent>
                    </Card>

                    <CategoryList accountExternalId={accountExternalId} categories={accountInfo.categories} />

                    <Card sx={{ marginBottom: '32px' }}>
                        <CardContent sx={{ display: 'flex' }}>
                            <ChartList stats={accountInfo.stats} />
                        </CardContent>
                    </Card>

                    <Card variant="outlined">
                        <TransactionsTable transactions={accountInfo.transactions} categories={accountInfo.categories} />
                    </Card>
                </>
            )}
        </div>
    );
};

export default Account;