import { Button, Card, CardContent, CardMedia, Tab, Tabs, Typography } from "@mui/material";
import { useNavigate } from "react-router-dom";
import { AccountDto } from "../../api/dtos";

type Props = {
    accounts: AccountDto[];
};

const AccountsCarousel = ({ accounts }: Props) => {
    const navigate = useNavigate();

    return (
        <Tabs variant="scrollable" scrollButtons>
            {accounts.map(account => (
                <Tab label={
                    <Card>
                        <CardContent>
                            <CardMedia
                                sx={{ height: 50, width: 50, margin: 'auto', marginBottom: '8px' }}
                                component="img"
                                image={account.institutionLogo}
                            />
                            <Typography variant="h5">{account.name !== null ? account.name : `${account.institutionName} account`}</Typography>
                            <Typography variant="subtitle1">{account.iban}</Typography>
                            <Typography variant="subtitle1">{`Currency: ${account.currency}`}</Typography>
                            <Button sx={{ marginTop: '8px' }} variant="outlined" onClick={() => navigate(`/accounts/${account.externalId}`)}>
                                View transactions
                            </Button>
                        </CardContent>
                    </Card>
                } />
            ))}
        </Tabs>
    );
};

export default AccountsCarousel;