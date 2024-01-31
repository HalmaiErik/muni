import { useEffect, useState } from "react";
import { AccountFullInfoDto, TransactionDto } from "../../api/dtos";
import { useParams } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";
import { getAccountFullInfo } from "../../api/bank-account-data-api";
import TransactionsTable from "../../components/transactions-table/TransactionsTable";
import { Card, CardContent, CardMedia, Chip, Paper, Stack } from "@mui/material";

const Account = () => {
    const [accountInfo, setAccountInfo] = useState<AccountFullInfoDto>();
    const { accountExternalId } = useParams();
    const { currentUser } = useAuth();

    useEffect(() => {
        if (accountExternalId && currentUser && currentUser.email) {
            getAccountFullInfo({ email: currentUser.email, accountExternalId, refresh: false })
                .then((data) => {
                    console.log(data);
                    setAccountInfo(data);
                })
                .catch();
        }
    }, []);

    return (
        <>
            {accountInfo && (
                <>
                    <Card sx={{ maxWidth: 1256, margin: 'auto', marginBottom: '32px' }} variant="elevation">
                        <CardContent>
                            <div style={{ display: "flex" }}>
                                <CardMedia
                                    sx={{
                                        height: 80,
                                        width: 80,
                                        alignSelf: 'center',
                                        flexDirection: 'column',
                                        marginRight: '24px'
                                    }}
                                    component="img"
                                    image={accountInfo.account.institutionLogo}
                                />
                                <Stack spacing={0.1}>
                                    <Stack direction="row" spacing={1.5}>
                                        <h1>{accountInfo.account.name}</h1>
                                        <Chip sx={{ alignSelf: 'center' }} label={accountInfo.account.status} color={accountInfo.account.status === 'ACTIVE' ? 'success' : 'error'} size="small" />
                                    </Stack>
                                    <p>{accountInfo.account.iban}</p>
                                    <p>{accountInfo.account.institutionName}</p>
                                    <p>{`Currency: ${accountInfo.account.currency}`}</p>
                                </Stack>
                            </div>
                        </CardContent>
                    </Card>

                    <Card sx={{ maxWidth: 1256, maxHeight: 512, margin: 'auto', overflow: 'auto' }} variant="outlined">
                        <TransactionsTable transactions={accountInfo.transactions} />
                    </Card>
                </>
            )}
        </>
    );
};

export default Account;